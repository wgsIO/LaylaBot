package dev.walk.gs.layla.commands

import dev.walk.gs.layla.LaylaDC
import dev.walk.gs.layla.LaylaLink
import dev.walk.gs.layla.events.CommandManager
import dev.walk.gs.layla.events.ReactionEvent
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.manager.infix.reactions
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.entities.User

class HelpCommand : CommandManager() {

    override fun onCommand() {
        sendHelper(user!!, layla!!)
        channel!!.sendMessage("${event!!.author.asMention} enviei uma instrução em seu privado, dê uma olhada :wink:").queue()

    }

    fun sendHelper(user: User, layla: SelfUser) {
        user!!.openPrivateChannel().queue {
            it message {
                var msg = embed {
                    ":stuck_out_tongue_closed_eyes: Ajuda da Layla :heart_eyes:" title null
                    "Olá ${user!!.asMention} eu sou a Layla, estou aqui para te ajudar em que você precisar." description false
                    "Meu objetivo é nada mais nada menos que lhe ajudar, eu fui criada para auxiliar servidores de discord... Eu tenho diversas funções para lhe entreter, posso ser configurada ao seu gosto." description false
                    "Adicione me em seu servidor clicando [aqui]($LaylaLink)." description false
                    "Dúvidas? Precisa de suporte? [Entre]($LaylaDC) no apartamento da __Layla__." description false
                    "Ajuda enviada direta do servidor: ${guild!!.name}" footer null
                    "Categoria:" field
                            ":computer: **Comandos essenciais**\n• `l!ajuda`, `l!info`, `l!perfil`, `l!configurar`, `l!anunciar`"
                    this thumbnail layla.avatarUrl
                    this randomColor 255
                }.prepare()
                msg.queue {
                    it reactions {
                        "\uD83D\uDCBB" action object : ReactionEvent() {

                        }
                    }
                }

            }
        }
    }

}