package dev.walk.gs.layla.manager.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.walk.gs.layla.manager.ServerManager
import dev.walk.gs.layla.manager.infix.complete
import dev.walk.gs.layla.manager.infix.deleteMsg
import dev.walk.gs.layla.manager.infix.message
import dev.walk.gs.layla.manager.layla.LaylaGuildMessages
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.managers.AudioManager

private var guild_music: MutableMap<Guild, WMusicManager> = mutableMapOf()

class WMusicManager(var channel: TextChannel, var guild: Guild, var member: Member, var layla: SelfUser) {

    var embed: EmbedBuilder = EmbedBuilder().setAuthor("Layla :tired_face:")
    var manager: PlayerManager? = null

    companion object {

        fun exists(guild: Guild): Boolean {
            return guild_music[guild] != null
        }

        fun hasConnected(guild: Guild): Boolean {
            return guild.audioManager.isConnected
        }

        fun get(guild: Guild): WMusicManager? {
            return guild_music[guild]
        }

        fun inMyChannel(channel: TextChannel, member: Member, manager: AudioManager): Boolean {
            var cchannel: GuildVoiceState? = member.voiceState
            if (cchannel?.channel != null && (cchannel.inVoiceChannel() || !cchannel.isDeafened)) {
                var voice: VoiceChannel? = cchannel.channel
                if (voice!!.id == manager.connectedChannel!!.id) {
                    return true
                } else {
                    LaylaGuildMessages.MusicMessages(channel).notConectedMyChannel(member.user)
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel).notConected(member.user)
            }
            return false
        }

        fun inMusicChannel(user: User, channel: TextChannel, guild: Guild, voice: VoiceChannel) : Boolean {

            var manager = ServerManager(guild.id)
            var channelID: String? = if (manager.hasDefinedData("MusicVoice")) manager.getData("MusicVoice") else null
            if (channelID != null) {
                var vchannel = guild.getVoiceChannelById(channelID)
                if (vchannel != null && channelID != voice.id) {
                    channel message "${user.asMention} conecte-se no canal de voz '${vchannel.name}' para conectar-me." complete 0 deleteMsg 5
                    return false
                }
            }
            return true

        }

        fun getMusicChannel(manager: AudioManager): VoiceChannel? {
            return manager.connectedChannel
        }

    }

    init {
        guild_music[guild] = this
    }

    fun disconnect() {
        var audioManager: AudioManager = guild.audioManager
        audioManager.closeAudioConnection()
    }

    fun connect(member: Member, status: ConnectionStatus) {
        var audioManager: AudioManager = guild.audioManager
        if (audioManager.isConnected) {
            if (inMyChannel(channel, member, audioManager)) {
                status.alreadyConnected()
            }
        } else {
            var cchannel: GuildVoiceState? = member.voiceState
            if (cchannel?.channel != null && (cchannel.inVoiceChannel() || !cchannel.isDeafened)) {
                var voice: VoiceChannel? = cchannel.channel
                if (!inMusicChannel(member.user, channel, channel.guild, voice!!)) { return }
                if (guild.selfMember.hasPermission(voice, Permission.VOICE_CONNECT)) {
                    audioManager.openAudioConnection(voice)
                    manager = PlayerManager(guild, member, channel)
                    manager!!.createPlayer()
                    LaylaGuildMessages.MusicMessages(channel).channelConected(member.user)
                    status.onSuccess()
                } else {
                    LaylaGuildMessages.MusicMessages(channel).notPermissionConnect()
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel).notConected(member.user)
            }
        }

    }

    fun addMusic(track: String) {
        manager?.playerManager!!.loadItemOrdered(this, track, object : AudioLoadResultHandler {
            override fun playlistLoaded(list: AudioPlaylist) {
                LaylaGuildMessages.MusicMessages(channel).addPlayList()
                list.tracks.forEach {
                    println(it.info.title)
                    manager!!.queue(it)
                }
            }

            override fun noMatches() {
            }

            override fun trackLoaded(track: AudioTrack) {
                LaylaGuildMessages.MusicMessages(channel).addSimple(track.info.title)
                manager!!.queue(track)
            }

            override fun loadFailed(p0: FriendlyException) {
                println(p0.message)
            }
        })
    }

    fun getChannel(): VoiceChannel? {
        return member.voiceState?.channel

    }

    //fun getChannel() : VoiceChannel? {
    //    return guild.voiceChannels.stream().filter({ voice -> voice.members.stream().filter({ member -> member.user.id == user.id }).findFirst().orElse(null) != null }).findFirst().orElse(null)
    //}


}

abstract class ConnectionStatus {

    open fun onSuccess() {}
    open fun alreadyConnected() {}

}


