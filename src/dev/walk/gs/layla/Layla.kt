package dev.walk.gs.layla

import carbon.walk.zking.core.util.TimeFormat
import dev.walk.gs.layla.commands.*
import dev.walk.gs.layla.events.*
import dev.walk.gs.layla.manager.music.searchs.Youtube
import dev.walk.gs.layla.utils.MultiValue
import dev.walk.sheetdb.manage.SheetDBManager
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.utils.Compression
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors

var sheetDB: SheetDBManager? = null
var builder: JDABuilder? = null
var JDAS: MutableList<JDA> = mutableListOf()

var showStatus: List<MultiValue<Activity.ActivityType, String>> = mutableListOf(
        MultiValue(Activity.ActivityType.STREAMING,"Olá, eu sou a Layla, minha prefix é 'L'"),
        MultiValue(Activity.ActivityType.WATCHING,"Adicione-me em seu servidor utilizando 'l!convite'"),
        MultiValue(Activity.ActivityType.WATCHING,"Utilize meus comandos digitando 'l!<função>'"),
        MultiValue(Activity.ActivityType.WATCHING,"Precisa de ajuda? utilize 'l!ajuda'"),
        MultiValue(Activity.ActivityType.STREAMING,"Fazem %temp que estou acordada"),
        MultiValue(Activity.ActivityType.STREAMING,"%g servidores me utilizar"),
        MultiValue(Activity.ActivityType.STREAMING,"%u usuários me utilizar"),
        MultiValue(Activity.ActivityType.DEFAULT,"alegria para todos! \uD83D\uDE0D"),
        MultiValue(Activity.ActivityType.DEFAULT,"volei com minhas amigas. \uD83D\uDE09 \nQuer participar? adicione-me em seu servidor com 'l!convite'"),
        MultiValue(Activity.ActivityType.WATCHING,"Estarei sendo atualizada constantemente, as vezes eu poderei estar offline."),
        MultiValue(Activity.ActivityType.DEFAULT,"e escutando música, deseja ouvir também? utilize 'l!play <nome/título/link>'")
)

fun print(value: String) {
    System.out.print("$value\n")
}

val Bot_Token = "SEU TOKEN"
val LaylaLink = "https://discordapp.com/api/oauth2/authorize?client_id=614481833769304074&permissions=8&scope=bot"
val LaylaDC = "https://discord.gg/dcdeuXE"
val JosefinoLink = "https://discordapp.com/oauth2/authorize?client_id=612806236060254219&scope=bot&permissions=8"
val Table_Name = "LaylaBot-Channels"
val Data_Featcher = "LaylaBot-Data"
val Cooldown_Value: Long = 10
val Max_Spam = 8

var YTSearch: Youtube = Youtube().apply { loadHandler("laylabot", "AIzaSyA6NLcj0xgY1bzYStz016EWKc49RHFLbBA") }

fun main(args: Array<String>) {
    builder = JDABuilder(AccountType.BOT)
    sheetDB = SheetDBManager(Paths.get("").toAbsolutePath().toString() + "/data/", "WGSLBot", "DWGSLPBot")
    Layla().startBot()
}

class Layla {

    fun startBot() {
        print("Inicializando a Layla, aguarde estaceber a conexão com o banco de dados...")
        if (!sheetDB!!.isConnected) {
            sheetDB!!.connect()
            print("Conectado ao banco de dados.")
        } else {
            print("Já estou conectada com o banco de dados.")
        }
        sheetDB!!.createTable(Table_Name)
        sheetDB!!.createTable(Data_Featcher)
        sheetDB!!.createFeather(Data_Featcher, "Blocked-Users")
        print("Autenticando a layla, aguarde...")
        builder!!.setToken(Bot_Token)
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.NONE)
                .setActivity(Activity.watching("Inicializando..."))
                .setAutoReconnect(true)
                .setCallbackPool(Executors.newSingleThreadScheduledExecutor())

                //Register events
                .addEventListeners(Commands())
                .addEventListeners(Listener())
                .addEventListeners(PrivateListener())
                .addEventListeners(ReactionsManager())

        //Register commands
        CommandManager.managerCommand("ajuda", "help").register(HelpCommand())
        CommandManager.managerCommand("ping", "lag", "lantencia").register(PingCommand())
        CommandManager.managerCommand("convite", "invite", "adicionar").register(InviteCommand())
        CommandManager.managerCommand("serverinfo", "info", "infomations", "servidorinfo").register(InformationCommand())
        CommandManager.managerCommand("config", "configurar").register(true, ConfigCommand())
        CommandManager.managerCommand("banir", "ban").register(null)
        CommandManager.managerCommand("kick", "kikar", "chutar").register(KickCommand())
        CommandManager.managerCommand("clear", "limpar").register(null)
        CommandManager.managerCommand("perfil").register(null)
        CommandManager.managerCommand("anunciar").register(null)
        CommandManager.managerCommand("backup").register(null)

        //Music commands
        CommandManager.managerCommand("tocar", "play").register(true, MusicCommand.PlayCommand())
        CommandManager.managerCommand("volume", "altura").register(true, MusicCommand.VolumeCommand())
        CommandManager.managerCommand("pular", "skip", "next", "proxima").register(true, MusicCommand.NextCommand())
        CommandManager.managerCommand("lista", "list", "queue", "playlist").register(true, MusicCommand.PlayListCommand())
        CommandManager.managerCommand("parar", "stop").register(true, MusicCommand.StopCommand())
        CommandManager.managerCommand("pausar", "pause").register(true, MusicCommand.PauseCommand())
        CommandManager.managerCommand("continuar", "continue").register(true, MusicCommand.unPauseCommand())
        CommandManager.managerCommand("conectar", "join").register(true, MusicCommand.JoinCommand())

        //for (i in 0..1) {
         //   JDAS.add(builder!!.useSharding(i, 3).build())
        //}
        JDAS.add(builder!!.useSharding(0, 1).build().awaitReady())
        //JDAS = mutableListOf(builder!!.build().awaitReady())

        print("Autenticada, pronto para o uso.")
        print("Atualizando atividade...")

        var init_T = TimeFormat.getCurrentTime()

        Timer().schedule(object : TimerTask() {
            override fun run() {
                var random = Random().nextInt(showStatus.size)
                var activity: Activity? = null
                var time = TimeFormat.getCurrentTime() - init_T
                if (time < 1) {
                    time = 1
                }
                JDAS.iterator().forEachRemaining {
                    if (it.status == JDA.Status.CONNECTED) {
                        if (activity == null) {
                            var guilds = 0; var users = 0
                            var statusdata = showStatus[random]
                            var status = statusdata.two; var type = statusdata.one
                            if (status.contains("%g")){ JDAS.iterator().forEachRemaining { guilds += it.guilds.size } }
                            if (status.contains("%u")){ JDAS.iterator().forEachRemaining { guilds += it.guilds.size } }
                            status = status.replace("%temp", TimeFormat.getTimeStringSimplified(time))
                                    .replace("%g", "$guilds").replace("%u", "$users")
                            activity = when (type) {
                                Activity.ActivityType.STREAMING -> Activity.streaming(status, null)
                                Activity.ActivityType.LISTENING -> Activity.listening(status)
                                Activity.ActivityType.DEFAULT -> Activity.playing(status)
                                else -> Activity.watching(status)
                            }
                        }
                        it.presence.activity = activity
                    }
                }
                print("Atividade alterada para: ${activity!!.name}")
            }
        }, 20, 50000)
        print("Atividade atualizada.")
    }
}