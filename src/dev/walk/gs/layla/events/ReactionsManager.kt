package dev.walk.gs.layla.events

import dev.walk.gs.layla.manager.BlockSystem
import dev.walk.gs.layla.manager.BlockType
import dev.walk.gs.layla.utils.MessageID
import dev.walk.gs.layla.utils.setMetaData
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ReactionRegister(var message: Message) {

    var owner: String? = null
    var one_execute: Boolean = false
    var custom_emote: Boolean = false
    fun ReactionRegister.owner(owner: String) {
        this.owner = owner
    }

    fun ReactionRegister.oneExecute(stats: Boolean) {
        one_execute = stats
    }

    infix fun String.action(event: ReactionEvent) {
        try {
            message.addReaction(this).queue {
                var emote = this
                if (custom_emote) {
                    emote = this.toLowerCase()
                }
                message.setMetaData("$emote-Event", event)
                message.setMetaData("$emote-Emote", emote)
                message.setMetaData("$emote-OneExecute", one_execute)
                message.setMetaData("$emote-CustomEmote", custom_emote)
                if (owner != null) {
                    message.setMetaData("Owner", owner!!)
                }
                event.onRegister(emote)
            }
        } catch (e: Exception) {
        }
    }

}

abstract class ReactionEvent {

    open fun onAddReaction(user: User) {}
    open fun onRemoveReaction(user: User) {}
    open fun onAddReactionBlock(user: User) {}
    open fun onRemoveReactionBlock(user: User) {}
    open fun onAddReaction(user: Member) {}
    open fun onRemoveReaction(user: Member) {}
    open fun onAddReactionBlock(user: Member) {}
    open fun onRemoveReactionBlock(user: Member) {}
    open fun onRegister(emote: String) {}

}

class ReactionsManager : ListenerAdapter() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        var user = event.user
        if (!user.isBot) {
            if (event.isFromGuild && BlockSystem.sendBlockedMessage(event.textChannel, user, BlockType.FUNCTION)) {
                return
            }
            var message = event.messageId
            var reaction = event.reaction
            var emote = reaction.reactionEmote

            var msg = MessageID(message)

            var emoji = emote.emoji
            if (msg.containsData("${emote.asCodepoints.toLowerCase()}-CustomEmote")) {
                emoji = emote.asCodepoints.toLowerCase()
            }
            var revent = if (msg.containsData("$emoji-Event")) {
                (msg.getMetaData("$emoji-Event") as ReactionEvent)
            } else {
                null
            }
            var one_execute = if (msg.containsData("$emoji-OneExecute")) {
                (msg.getMetaData("$emoji-OneExecute") as Boolean)
            } else {
                false
            }
            var executes = if (msg.containsData("$emoji-${user.id}-EExecutes")) {
                (msg.getMetaData("$emoji-${user.id}-EExecutes") as Int)
            } else {
                0
            }

            if (one_execute && executes > 0) {
                return
            }

            if (msg.containsData("$emoji-Emote") && msg.getMetaData("$emoji-Emote") == emoji) {
                if (msg.containsData("Owner")) {
                    if (msg.getMetaData("Owner") == event.user.id) {
                        if (event.isFromGuild) {
                            revent!!.onAddReaction(event.member!!)
                        }
                        revent!!.onAddReaction(user)
                        msg.setMetaData("$emoji-${user.id}-EExecutes", (executes + 1))
                    } else {
                        if (event.isFromGuild) {
                            revent!!.onAddReactionBlock(event.member!!)
                        }
                        revent!!.onAddReactionBlock(user)
                    }
                } else {
                    if (event.isFromGuild) {
                        revent!!.onAddReaction(event.member!!)
                    }
                    revent!!.onAddReaction(user)
                    msg.setMetaData("$emoji-${user.id}-EExecutes", (executes + 1))
                }
            }

        }
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        var user = event.user
        if (!user.isBot) {
            if (event.isFromGuild && BlockSystem.sendBlockedMessage(event.textChannel, user, BlockType.FUNCTION)) {
                return
            }
            var message = event.messageId
            var reaction = event.reaction
            var emote = reaction.reactionEmote

            var msg = MessageID(message)

            var emoji = emote.emoji
            var revent = if (msg.containsData("$emoji-Event")) {
                (msg.getMetaData("$emoji-Event") as ReactionEvent)
            } else {
                null
            }
            var one_execute = if (msg.containsData("$emoji-OneExecute")) {
                (msg.getMetaData("$emoji-OneExecute") as Boolean)
            } else {
                false
            }
            var executes = if (msg.containsData("$emoji-${user.id}-RExecutes")) {
                (msg.getMetaData("$emoji-${user.id}-RExecutes") as Int)
            } else {
                0
            }

            if (one_execute && executes > 0) {
                return
            }

            if (msg.containsData("$emoji-Emote") && msg.getMetaData("$emoji-Emote") == emoji) {
                if (msg.containsData("Owner")) {
                    if (msg.getMetaData("Owner") == event.user.id) {
                        if (event.isFromGuild) {
                            revent!!.onRemoveReaction(event.member!!)
                        }
                        revent!!.onRemoveReaction(user)
                        msg.setMetaData("$emoji-${user.id}-RExecutes", (executes + 1))
                    } else {
                        if (event.isFromGuild) {
                            revent!!.onRemoveReactionBlock(event.member!!)
                        }
                        revent!!.onRemoveReactionBlock(user)
                    }
                } else {
                    if (event.isFromGuild) {
                        revent!!.onRemoveReaction(event.member!!)
                    }
                    revent!!.onRemoveReaction(user)
                    msg.setMetaData("$emoji-${user.id}-RExecutes", (executes + 1))
                }
            }

        }
    }

}