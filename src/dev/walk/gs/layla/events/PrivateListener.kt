package dev.walk.gs.layla.events

import dev.walk.gs.layla.manager.ServerConfiguration
import dev.walk.gs.layla.manager.getGuildEditting
import dev.walk.gs.layla.manager.hasEditingServer
import dev.walk.gs.layla.manager.infix.deleteMsg
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.manager.layla.LaylaTitles
import dev.walk.gs.layla.utils.ParseType
import dev.walk.gs.layla.utils.parse
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.util.*

class PrivateListener : ListenerAdapter() {

    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        var user = event.author
        if (!user.isBot && hasEditingServer(user)) {

            val message = event.message
            val msg = message.contentRaw
            val channel = event.channel

            val args: Array<String> = msg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var guild = getGuildEditting(user)
            var config = ServerConfiguration.get(user)

            var status = config!!.getStatus()

            if (status.contains("Menu")){
                channel message {
                    embed {
                        ":no_entry_sign: Ocorreu um erro ao efetuar esta ação, motivo:" title null
                        "Você deve selecionar algo à modificar. :triumph:" description false
                    } pqueue 0
                }
                return
            }

            var ctext = false; var cvoice = false
            if (status.contains("Channel")) ctext = true else if (status.contains("Voice")) cvoice = true
            if (ctext || cvoice){
                var value = parse(ParseType.LONG, args[0])
                if (value.one > 0) {
                    var sc = guild!!.getGuildChannelById(value.one)
                    if (sc != null && sc.type == if (ctext) ChannelType.TEXT else ChannelType.VOICE) {
                        channel message {
                            embed {
                                LaylaTitles.CONFIGSUCESS.title title null
                                "**Dados do canal:** ID: ${args[0]} Nome: ${sc.name}" description false
                            }.prepare().queue{ it deleteMsg 5 }
                        }
                    } else {
                        channel message {
                            embed {
                                LaylaTitles.ERROR.title title null
                                "Canal não existe ou não é do tipo ${if (ctext) "texto" else "voz"}. :grimacing:" description false
                            }.prepare().queue{ it deleteMsg 5 }
                        }
                        return
                    }
                } else {
                    channel message {
                        embed {
                            LaylaTitles.ERROR.title title null
                            "Insira um ID de canal de ${if (ctext) "texto" else "voz"} válido. :face_palm:" description false
                        }.prepare().queue{ it deleteMsg 5 }
                    }
                    return
                }
            } else {
                channel message {
                    embed {
                        LaylaTitles.CONFIGSUCESS.title title null
                    }.prepare().queue{ it deleteMsg 5 }
                }
            }

            config.setDataValue(args[0]).save().saveModify()

            /**if (config!!.getStatus() == "CommandChannel") {
                var value = parse(ParseType.LONG, args[0])
                if (value.one > 0) {
                    var sc = guild!!.getGuildChannelById(value.one)
                    if (sc != null && sc.type == ChannelType.TEXT) {
                        config.setDataValue(args[0]).save().saveModify()
                        eb.setTitle(":white_check_mark: Configuração definida com sucesso.")
                                .setDescription("**Dados do canal:** ID: ${args[0]} Nome: ${sc.name}")
                    } else {
                        eb.setTitle(":no_entry_sign: Ocorreu um erro ao efetuar esta ação, motivo: ")
                                .setDescription("Canal não existe ou não é de texto. :grimacing:")
                    }
                } else {
                    eb.setTitle(":no_entry_sign: Ocorreu um erro ao efetuar esta ação, motivo: ")
                            .setDescription("Insira um ID de canal de texto válido. :face_palm:")
                }
            } else {
                eb.setTitle(":no_entry_sign: Ocorreu um erro ao efetuar esta ação, motivo: ")
                        .setDescription("Você deve selecionar algo à modificar. :triumph:")
            }
            **/
        }
    }

}