package dev.walk.gs.layla.commands

import dev.walk.gs.layla.events.CommandManager
import dev.walk.gs.layla.events.ReactionEvent
import dev.walk.gs.layla.manager.infix.deleteMsg
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.manager.infix.reactions
import dev.walk.gs.layla.manager.layla.LaylaGuildMessages
import dev.walk.gs.layla.manager.layla.LaylaTitles
import dev.walk.gs.layla.utils.join
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User

class KickCommand : CommandManager() {

    override fun updateClass() {
        permission = Permission.KICK_MEMBERS
    }

    override fun onCommand() {
        //l!kick <user> <motive>
        var args = args!!.join(1, " ").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var messagers = LaylaGuildMessages.AdminMessages(channel!!)
        if (args.isNotEmpty()){
            var member = guild!!.getMemberById(args[0].replace("<", "").replace("@", "").replace(">", ""))
            var motive = "desconhecido."
            if (args.size >= 2) {
                motive = args.join(1, " ")
            }
            channel!! message {
                var msg = embed {
                    LaylaTitles.KICK.title.replace("%owner", event!!.member!!.effectiveName).replace("%user", member!!.effectiveName) title null
                    "Confirme se realmente deseja chutar ${member!!.effectiveName} reagindo com \uD83D\uDC62." description false
                } complete 0
                msg reactions {
                    owner(user!!.id)
                    "\uD83D\uDC62" action object: ReactionEvent(){

                        override fun onAddReaction(user: User) {
                            msg deleteMsg 0
                            try {
                                member!!.roles.iterator().forEachRemaining {
                                    it.position
                                }
                                //guild!!.kick(member!!).complete()
                               // messagers.kickedUser(event!!.member!!, member!!, motive)
                            } catch (e: Exception){
                                messagers.notKick(event!!.member!!, member!!)
                            }

                        }

                    }
                }
            }
        } else {
            messagers.kickArgument(user!!)
        }
    }

}