package dev.walk.gs.layla.manager

import dev.walk.gs.layla.Data_Featcher
import dev.walk.gs.layla.LaylaLink
import dev.walk.gs.layla.Max_Spam
import dev.walk.gs.layla.events.spam
import dev.walk.gs.layla.manager.infix.deleteMsg
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.sheetDB
import dev.walk.sheetdb.manage.SheetResult
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.*

enum class BlockType(var value: String) {
    FUNCTION("minhas funções"),
    COMMAND("meus comandos")
}

class BlockSystem {

    companion object {

        private var query: SheetResult = sheetDB!!.query(Data_Featcher, "Blocked-Users")

        fun checkSpamm(channel: TextChannel, user: User): Boolean {
            var u_spam = spam[user]
            if (u_spam != null) {
                spam[user] = u_spam + 1
            } else {
                spam[user] = 1
            }
            u_spam = spam[user]
            when (u_spam) {
                (Max_Spam / 2) -> {
                    channel message {
                        embed {
                            "Layla :tired_face:" title null
                            "${user.asMention} Você está spammando minhas funções, por favor, pare! :pray:" description false
                            "Eu tenho um sistema em que detecta se está spammando minhas funções." description false
                            "E você está spammando, se você continuar será bloqueado permanentemente de utilizar minhas funções! :confused:" description false
                            this randomColor 255
                        } complete 0 deleteMsg 5
                    }
                    return true
                }
                Max_Spam -> {
                    channel message {
                        embed {
                            "Layla :rage:" title null
                            "${user.asMention} você foi bloqueado de utilizar meus comandos." description false
                            this randomColor 255
                        } complete 0 deleteMsg 5
                    }
                    BlockSystem.blockUser(user)
                    return true
                }
            }

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    if (u_spam != null) {
                        spam[user] = spam[user]!! - 1
                    }
                }
            }, 4000)

            return false
        }

        fun sendBlockedMessage(channel: TextChannel, user: User, type: BlockType): Boolean {
            if (BlockSystem.hasBlocked(user)) {
                if (spam[user] == null) {
                    channel message {
                        embed {
                            "Layla :tired_face:" title null
                            "${user.asMention} Você está bloqueado de utilizar ${type.value}. :pensive:" description false
                            "Bloqueado injustamente? Contate um suporte para reaver sua situação." description false
                            "Entre em contato com um suporte clicando [aqui](${LaylaLink})." description false
                            "Agora te ignorarei até que você seja desbloqueado ou que eu seja reiniciada." description false
                            "• Mesmo eu reiniciando você continuará bloqueado!" description false
                        } complete 0 deleteMsg 5
                    }
                    spam[user] = 1
                }
                return true
            }
            return false
        }

        fun blockUser(user: User) {
            var executor = query.get(user.id)
            if (executor.isNull(user.id)) {
                query.insert(user.id).setValue(true).close()
            } else {
                query.update(user.id).setValue(true).close()
            }
        }

        fun unblockUser(user: User) {
            var executor = query.get(user.id)
            if (!executor.isNull(user.id)) {
                query.update(user.id).setValue(false).close()
            }
        }

        fun hasBlocked(user: User): Boolean {
            var executor = query.get(user.id)
            if (!executor.isNull(user.id)) {
                return executor.getBooleanOrFalse(user.id)
            }
            return false
        }

    }

}