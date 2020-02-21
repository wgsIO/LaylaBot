package dev.walk.gs.layla.commands

import dev.walk.gs.layla.JosefinoLink
import dev.walk.gs.layla.LaylaLink
import dev.walk.gs.layla.events.CommandManager
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.manager.infix.queue

class InviteCommand : CommandManager() {

    override fun onCommand() {
        user!!.openPrivateChannel().queue({ channel ->
            channel message {
                embed {
                    "Deseja conhecer a Layla? :grimacing:" title null
                    "Olá ${user!!.asMention} eu sou a Layla, deseja me adicionar?" description false
                    "Então, para me adicionar em seu servidor clique [aqui]($LaylaLink)" description false
                    "Eu tenho diversas funções para lhe entreter, você pode receber uma instrução utilizando o comando __$prefix!ajuda__." description false
                    "\nJá conhece um velho amigo meu? o Josefino, então, para adicionar ele clique [aqui]($JosefinoLink)" description false
                    "O mesmo tem a função de anunciar no privado de todos membros de um determinado servidor." description false
                    "Adicionar: " field "• [__Layla__]($LaylaLink)\n• [__Josefino__]($JosefinoLink)"
                    this thumbnail layla!!.avatarUrl
                    this randomColor 255
                    layla!!.name footer layla!!.avatarUrl!!
                }.prepare() queue 0
            }
        })
    }

}