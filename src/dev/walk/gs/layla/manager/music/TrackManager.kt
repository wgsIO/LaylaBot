package dev.walk.gs.layla.manager.music

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.managers.AudioManager

object TrackManager {

    var self: SelfUser? = null
    var channel: TextChannel? = null
    var guild: Guild? = null

    fun selectShard(user: SelfUser): TrackManager {
        self = user
        return this
    }

    fun selectChannel(channel: TextChannel): TrackManager {
        this.channel = channel
        return this
    }

    fun selectGuild(guild: Guild): TrackManager {
        this.guild = guild
        return this
    }

    fun getMusicManager(member: Member?, createIfNotExist: Boolean): WMusicManager {
        var wmusic = WMusicManager.get(guild!!)
        if (wmusic == null && createIfNotExist) {
            wmusic = WMusicManager(channel!!, guild!!, member!!, self!!)
        }
        return wmusic!!
    }

    fun addTrack(member: Member, track: String, force_run: Boolean) {
        var wmusic = getMusicManager(member, force_run)
        if (force_run) {
            wmusic.connect(member, object : ConnectionStatus() {})
        }
        wmusic.addMusic(track)
    }

    fun nextTrack(member: Member) {
        var wmusic = getMusicManager(member, false)
        wmusic.manager!!.nextTrack()
    }

    fun stopMusic(member: Member) {
        var wmusic = getMusicManager(member, false)
        wmusic.manager!!._stop()
    }

    fun pauseMusic(member: Member) {
        var wmusic = getMusicManager(member, false)
        wmusic.manager!!._pause()
    }

    fun unPauseMusic(member: Member) {
        var wmusic = getMusicManager(member, false)
        wmusic.manager!!._continue()
    }

    fun setVolume(member: Member, volume: Int) {
        var wmusic = getMusicManager(member, false)
        wmusic.manager!!._setVolume(volume)
    }

    fun getChannel(member: Member): VoiceChannel? {
        return member.voiceState?.channel
    }

    fun isPaused() = getMusicManager(null, false).manager!!.player!!.isPaused

    fun getTracks() = getMusicManager(null, false).manager!!.queue.toList()

    fun getTrack() = getMusicManager(null, false).manager!!.player!!.playingTrack

    fun getMusicChannel(audioManager: AudioManager) = WMusicManager.getMusicChannel(audioManager)

    fun desconnect() = getMusicManager(null, false).disconnect()

}