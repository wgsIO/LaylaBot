package dev.walk.gs.layla.events

import carbon.walk.zking.core.util.TimeFormat
import dev.walk.gs.layla.Cooldown_Value
import dev.walk.gs.layla.Table_Name
import dev.walk.gs.layla.manager.BlockSystem
import dev.walk.gs.layla.manager.BlockType
import dev.walk.gs.layla.manager.ServerManager
import dev.walk.gs.layla.manager.infix.complete
import dev.walk.gs.layla.manager.infix.deleteMsg
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.print
import dev.walk.gs.layla.sheetDB
import dev.walk.gs.layla.utils.CooldownManager
import dev.walk.gs.layla.utils.MultiValue
import dev.walk.gs.layla.utils.join
import dev.walk.sheetdb.manage.SheetResult
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

var spam: MutableMap<User, Int> = mutableMapOf()

class Commands : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!event.author.isBot) {
            var ms = System.currentTimeMillis()

            var gID = event.guild.id
            sheetDB!!.createFeather(Table_Name, gID)
            var query = sheetDB!!.query(Table_Name, gID)
            var getters = query.get("Prefix", "CommandChannel")
            var prefix = "l"
            if (!getters.isNull("Prefix")) {
                prefix = getters.getString("Prefix")
            }

            val message = event.message
            val msg = message.contentRaw
            val channel = event.channel
            val args: Array<String> = msg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var cooldown = CooldownManager(event.author)
            if (msg.startsWith("$prefix!", true)) {

                var user = event.author

                if (BlockSystem.sendBlockedMessage(channel, user, BlockType.COMMAND)) {
                    return
                }
                if (BlockSystem.checkSpamm(channel, user)) {
                    return
                }

                message deleteMsg 2

                if (cooldown.hasCooldown()) {
                    channel message ":shrug:  ${user.asMention} aguarde ${TimeFormat.getTimeString(cooldown.getRestantCooldown())} para utilizar meus comandos novamente." complete 0 deleteMsg 5
                    return
                }

                var manager = ServerManager(gID)
                var channelID: String? = if (manager.hasDefinedData("CommandChannel")) manager.getData("CommandChannel") else null

                val guild = event.guild
                var cmd = args[0].replace("$prefix!", "").toLowerCase()

                if (channelID != null && guild.getTextChannelById(channelID) != null && channelID != channel.id && !(commands[cmd] != null && commands[cmd]!!.one)) {
                    channel message "${user.asMention} não é permitido utilizar meus comandos neste canal." complete 0 deleteMsg 5
                    return
                }

                var command = commands[cmd]?.two
                if (command != null) {
                    try {
                        var clazz = command.javaClass.newInstance()
                        clazz.user = user
                        clazz.event = event
                        clazz.guild = guild
                        clazz.guildID = guild.id
                        clazz.prefix = prefix
                        clazz.message = message
                        clazz.raw = msg
                        clazz.channel = channel
                        clazz.args = args
                        clazz.cooldown = cooldown
                        clazz.jda = event.jda
                        clazz.layla = event.jda.selfUser
                        clazz.query = query
                        clazz.updateClass()
                        if (clazz.permission != null && !clazz.hasPermission(clazz.permission!!)) {
                            channel message "${user.asMention} você não tem permissão para utilizar esta função." complete 0 deleteMsg 5
                            return
                        }
                        cooldown.createCooldown(Cooldown_Value)
                        clazz.onCommand()
                    } catch (e: Exception) {
                        channel message "\uD83D\uDE2D ${user.asMention} ocorreu um erro ao executar este comando, contate um suporte." complete 0 deleteMsg 5
                        print("\nOcorreu um erro {\n" +
                                "Guild: ${guild.name} | ${guild.id}\n" +
                                "Shard: ${event.jda.shardInfo.shardId}\n" +
                                "Canal: ${channel.name}\n" +
                                "Comando: $prefix!$cmd -> $cmd\n" +
                                "Argumentos: ${args.join(1, " ")}\n" +
                                "Linha: ${e.localizedMessage} | Causa: ${e.cause}\n" +
                                "}\n"
                        )
                    }
                } else {
                    if (commands.containsKey(cmd)) {
                        channel message "\uD83E\uDD10 ${user.asMention} comando está em manuntenção, por favor aguarde a manunteção terminar..." complete 0 deleteMsg 5
                    } else {
                        channel message "${user.asMention} comando não encontrado :sob:, utilize __$prefix!ajuda__ para eu lhe ajudar." complete 0 deleteMsg 5
                    }
                }

                //MS TESTER
                print("Tempo de resposta: ${System.currentTimeMillis() - ms} ms")

            }
        }
    }

}

private var commands: MutableMap<String, MultiValue<Boolean, CommandManager?>> = mutableMapOf()

open class CommandManager : LaylaCommand {

    var user: User? = null
    var event: GuildMessageReceivedEvent? = null
    var guild: Guild? = null
    var guildID: String = "0"
    var prefix: String = "l"
    var message: Message? = null
    var raw: String? = null
    var channel: TextChannel? = null
    var args: Array<String> = arrayOf()
    var cooldown: CooldownManager? = null
    var jda: JDA? = null
    var layla: SelfUser? = null
    var query: SheetResult? = null

    var embed = EmbedBuilder()
    var random = Random()

    var permission: Permission? = null

    companion object {
        fun managerCommand(vararg commands: String): MCommands {
            return MCommands(commands)
        }
    }

    fun hasPermission(permission: Permission): Boolean {
        return hasPermissions(permission)
    }

    fun hasPermissions(vararg permissions: Permission): Boolean {
        var has = false
        var member = guild!!.getMember(user!!)
        permissions.iterator().forEachRemaining {
            has = member!!.hasPermission(it)
        }
        return has
    }

}

class MCommands {

    var cmds: Array<out String>

    constructor(cmds: Array<out String>) {
        this.cmds = cmds
    }

    fun register(clazz: CommandManager?) = register(false, clazz)

    fun register(cmd_bypass: Boolean, clazz: CommandManager?) {
        cmds.iterator().forEachRemaining {
            if (!commands.contains(it)) {
                commands[it.toLowerCase()] = MultiValue(cmd_bypass, clazz)
            }
        }
    }

    fun unregister() {
        cmds.iterator().forEachRemaining {
            if (commands.contains(it)) {
                commands.remove(it.toLowerCase())
            }
        }
    }

}

interface LaylaCommand {

    fun updateClass() {}
    fun onCommand() {}

}