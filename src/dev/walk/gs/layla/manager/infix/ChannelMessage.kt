package dev.walk.gs.layla.manager.infix

import dev.walk.gs.layla.events.ReactionRegister
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.awt.Color
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

infix fun TextChannel.message(method: ChannelMessage.() -> Unit) = ChannelMessage(this).apply(method)
infix fun PrivateChannel.message(method: ChannelMessage.() -> Unit) = ChannelMessage(this).apply(method)
infix fun TextChannel.message(message: String) = sendMessage(message)
infix fun PrivateChannel.message(message: String) = sendMessage(message)
infix fun MessageAction.queue(time: Long) {
    if (time > 0) {
        this.queueAfter(time, TimeUnit.SECONDS)
    } else {
        this.queue()
    }
}

infix fun MessageAction.complete(time: Long): Message = if (time > 0) {
    this.completeAfter(time, TimeUnit.SECONDS)
} else {
    this.complete()
}

infix fun Message.deleteMsg(time: Long) {
    if (time > 0) {
        this.delete().queueAfter(time, TimeUnit.SECONDS)
    } else {
        this.delete().queue()
    }
}

infix fun Message.reactions(method: ReactionRegister.() -> Unit): Message {
    ReactionRegister(this).apply(method)
    return this
}

infix fun Message.edit(message: String) = this.editMessage(message)
infix fun Message.edit(message: MessageEmbed) = this.editMessage(message)

class ChannelMessage() {

    var channel: TextChannel? = null
    var privateChannel: PrivateChannel? = null

    constructor(channel: TextChannel) : this() {
        this.channel = channel
    }

    constructor(channel: PrivateChannel) : this() {
        this.privateChannel = channel
    }

    infix fun ChannelMessage.embed(method: MessageEmbed.() -> Unit) = MessageEmbed().apply(method)

    class MessageEmbed {

        var builder = EmbedBuilder()
        private var random = Random()

        private var undefined_value = "Indefined"

        var desc = undefined_value


        infix fun String.title(url: String?) = builder.setTitle(this, url)
        infix fun String.author(url: String?) = builder.setAuthor(this, url)
        infix fun String.field(content: String) = builder.addField(this, content, false)
        infix fun String.fieldline(content: String) = builder.addField(this, content, true)
        infix fun String.footer(url: String?) = builder.setFooter(this, url)

        infix fun MessageEmbed.image(url: String?) = builder.setImage(url)
        infix fun MessageEmbed.thumbnail(url: String?) = builder.setThumbnail(url)
        infix fun MessageEmbed.color(color: Color) = builder.setColor(color)
        infix fun MessageEmbed.randomColor(max: Int) = this color Color(random.nextInt(max), random.nextInt(max), random.nextInt(max))
        infix fun MessageEmbed.timestamp(zone: ZonedDateTime) = builder.setTimestamp(zone)

        infix fun String.description(clear: Boolean) {
            if (clear || desc == undefined_value) {
                desc = ""
            }
            desc += "$this\n"
            builder.setDescription(desc)
        }

    }

    infix fun MessageEmbed.queue(time: Long) {
        if (time > 0) {
            channel!!.sendMessage(this.builder.build()).queueAfter(time, TimeUnit.SECONDS)
        } else {
            channel!!.sendMessage(this.builder.build()).queue()
        }
    }

    fun MessageEmbed.qprepare(): MessageAction {
        return channel!!.sendMessage(this.builder.build())
    }

    infix fun MessageEmbed.complete(time: Long) = if (time > 0) {
        (channel as TextChannel).sendMessage(this.builder.build()).completeAfter(time, TimeUnit.SECONDS)
    } else {
        (channel as TextChannel).sendMessage(this.builder.build()).complete()
    }

    fun MessageEmbed.prepare(): MessageAction {
        return privateChannel!!.sendMessage(this.builder.build())
    }

    infix fun MessageEmbed.pqueue(time: Long): Message? {
        privateChannel!!.sendMessage(this.builder.build()).queueAfter(time, TimeUnit.SECONDS) {
            return@queueAfter
        }
        return null
    }

}