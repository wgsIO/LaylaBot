package dev.walk.gs.layla.commands

import dev.walk.gs.layla.Cooldown_Value
import dev.walk.gs.layla.LaylaDC
import dev.walk.gs.layla.events.CommandManager
import dev.walk.gs.layla.events.ReactionEvent
import dev.walk.gs.layla.manager.ServerConfiguration
import dev.walk.gs.layla.manager.hasEditingServer
import dev.walk.gs.layla.manager.infix.*
import dev.walk.gs.layla.manager.users_config
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.restaction.MessageAction

class ConfigCommand : CommandManager() {

    override fun updateClass() {
        permission = Permission.ADMINISTRATOR
    }

    override fun onCommand() {
        if (users_config[guild!!] != null) {
            channel!! message "${user!!.asMention} já estou sendo configurada neste instante." complete 0 deleteMsg 5
            return
        }
        channel!! message "${user!!.asMention} certo, vamos configurar-me do jeito ideal para seu servidor. :computer:\n Dê uma visualizada em seu privado. :wink:" complete 0 deleteMsg 5
        sendMainMessage(user!!, layla!!)
        cooldown!!.createCooldown(Cooldown_Value)

    }

    fun sendMainMessage(user: User, layla: SelfUser) {
        var icon = guild!!.iconUrl
        if (icon == null) {
            icon = layla!!.avatarUrl
        }
        var config: ServerConfiguration?
        if (!hasEditingServer(user)) {
            config = ServerConfiguration(user).setServerConfig(guild!!, "Menu")
        } else {
            config = ServerConfiguration.Companion.get(user)
            config!!.setServerConfig(config.getGuild(), "Menu")
        }
        user.openPrivateChannel().queue({ ch ->
            ch message {
                var msg = embed {
                    this image config.getGuild().iconUrl
                    "Certo, vamos começar a configurar a Layla? :thinking: " title null
                    "Olá ${user.asMention} vamos configurar a layla para deixar ideal para seu servidor?" description false
                    "\nEntão, para começar você reage com o ícone de acordo com que você deseja configurar." description false
                    "Vale lembrar que a minha prefix é 'L', caso você tenha dúvida entre em meu discord: [Entrar]($LaylaDC)." description false
                    "\nVamos começar a configuração, para iniciar, basta clicar em um ícone de acordo com o que deseja configurar." description false
                    "Lembrando que toda alteração é salva imediatamente." description false
                    "O que você deseja configurar?" field
                            "• :computer: '**Canal de Comando**' ( Canal permitido a utilização de meus comandos )\n" +
                            "• :x: '**Cancelar Configuração**' ( Cancelará a configuração do bot )"
                    this thumbnail layla.avatarUrl
                    this randomColor 255
                    guild!!.name footer icon!!
                }.prepare()
                msg.queue {
                    it reactions {
                        //Channel Command Config
                        "\uD83D\uDCBB" action object : ReactionEvent() {
                            override fun onAddReaction(user: User) {
                                if (config.getStatus() == "Menu") {
                                    config.setStatus("CommandChannel")
                                    it deleteMsg 0
                                    ch message {
                                        msg = embed {
                                            "Alteração no canal de comandos" title null
                                            "${user.asMention} certo, vamos configurar o canal de utilização de meus comandos. :thumbsup:" description false
                                            "• Digite o __ID__ do canal à definir como local de meus comandos." field "Caso queira cancelar basta reagir com \uD83D\uDD19 para voltar ao menu."
                                            guild!!.name footer icon!!
                                        }.prepare()
                                        msg.queue {
                                            it reactions {
                                                "\uD83D\uDD19" action object : ReactionEvent() {
                                                    override fun onAddReaction(user: User) {
                                                        if (config.getStatus() == "CommandChannel") {
                                                            it deleteMsg 0
                                                            sendMainMessage(user, layla)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //Music Configs
                        "\uD83C\uDFB5" action object : ReactionEvent() {
                            override fun onAddReaction(user: User) {
                                if (config.getStatus() == "Menu") {
                                    it deleteMsg 0
                                    sendMusicMainMessage(config, ch, icon!!)
                                }
                            }
                        }

                        //Cancel Config
                        "❌" action object : ReactionEvent() {
                            override fun onAddReaction(user: User) {
                                if (config.getStatus() == "Menu") {
                                    config.cancel()
                                    it deleteMsg 0
                                    ch message {
                                        embed {
                                            ":computer: Configuração cancelada." title null
                                            "Todas ações modificas salvas, espero que tenha ficado ao seu gosto :laughing:" description false
                                        }.prepare() queue 0
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun sendMusicMainMessage(config: ServerConfiguration, ch: PrivateChannel, icon: String){
        config.setStatus("MusicMenu")
        ch message {
            var msg = embed {
                this thumbnail layla!!.avatarUrl
                "Configurar o sistema de música" title null
                "${user!!.asMention} oque você deseja configurar no sistema de música?" description false
                "• Para configurar basta selecionar oquê você deseja configurar." field "Caso queira cancelar basta reagir com \uD83D\uDD19 para voltar ao menu."
                "\n`Configurar:`" field " \uD83D\uDCBB - **Canal de comandos** ( Para configurar onde será possível utilizar o sistema de música)\n" +
                        "\uD83C\uDFB5 - **Canal de música** ( Para configurar onde será o canal de música )\n" +
                        "\n **Observação:** Caso não configurar será possível usar os comandos no canal de comandos e o bot conectará no canal que o membro tiver conectado."
                guild!!.name footer icon
            }.prepare()
            msg.queue {
                it reactions {

                    "\uD83D\uDCBB" action object : ReactionEvent() {
                        override fun onAddReaction(user: User) {
                            if (config.getStatus() == "MusicMenu") {
                                config.setStatus("MusicChannel")
                                it deleteMsg 0
                                ch message {
                                    msg = embed {
                                        "Alteração no canal de comandos do sistema de música" title null
                                        "${user.asMention} certo, vamos configurar o canal de utilização dos comandos de música. :thumbsup:" description false
                                        "• Digite o __ID__ do canal à definir como local de comandos de música." field "Caso queira cancelar basta reagir com \uD83D\uDD19 para voltar."
                                        guild!!.name footer icon!!
                                    }.prepare()
                                    msg.queue {
                                        it reactions {
                                            "\uD83D\uDD19" action object : ReactionEvent() {
                                                override fun onAddReaction(user: User) {
                                                    if (config.getStatus() == "MusicChannel") {
                                                        it deleteMsg 0
                                                        sendMusicMainMessage(config, ch, icon)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "\uD83C\uDFB5" action object : ReactionEvent() {
                        override fun onAddReaction(user: User) {
                            if (config.getStatus() == "MusicMenu") {
                                config.setStatus("MusicVoice")
                                it deleteMsg 0
                                ch message {
                                    msg = embed {
                                        "Alteração no canal de música" title null
                                        "${user.asMention} certo, vamos configurar o canal de música. :thumbsup:" description false
                                        "• Digite o __ID__ do canal à definir como canal de música." field "Caso queira cancelar basta reagir com \uD83D\uDD19 para voltar."
                                        guild!!.name footer icon!!
                                    }.prepare()
                                    msg.queue {
                                        it reactions {
                                            "\uD83D\uDD19" action object : ReactionEvent() {
                                                override fun onAddReaction(user: User) {
                                                    if (config.getStatus() == "MusicVoice") {
                                                        it deleteMsg 0
                                                        sendMusicMainMessage(config, ch, icon)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "\uD83D\uDD19" action object : ReactionEvent() {
                        override fun onAddReaction(user: User) {
                            if (config.getStatus() == "MusicMenu") {
                                it deleteMsg 0
                                sendMainMessage(user, layla!!)
                            }
                        }
                    }
                }
            }
        }
    }

}