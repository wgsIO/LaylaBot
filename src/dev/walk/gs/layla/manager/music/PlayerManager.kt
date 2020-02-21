package dev.walk.gs.layla.manager.music

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.walk.gs.layla.handlers.AudioPlayerHandler
import dev.walk.gs.layla.manager.layla.LaylaGuildMessages
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.LinkedBlockingQueue

class PlayerManager(var guild: Guild, var author: Member, var channel: TextChannel) : AudioEventAdapter() {

    var playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    var player: AudioPlayer? = null
    var handler: AudioPlayerHandler? = null

    val queue = LinkedBlockingQueue<AudioTrack>()

    init {
        playerManager.configuration.resamplingQuality = AudioConfiguration.ResamplingQuality.MEDIUM
        playerManager.registerSourceManager(YoutubeAudioSourceManager())
        playerManager.registerSourceManager(SoundCloudAudioSourceManager())
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
    }

    fun createPlayer() {
        player = playerManager.createPlayer()
        player!!.addListener(this)
        handler = AudioPlayerHandler(player!!)
        guild.audioManager.sendingHandler = handler
    }

    fun _stop() {
        player!!.stopTrack()
        queue.clear()
        _continue()
    }

    fun _continue() = player!!.apply { isPaused = false }
    fun _pause() = player!!.apply { isPaused = true }
    fun _setVolume(value: Int) = player!!.apply { volume = value }

    fun queue(track: AudioTrack) {
        if (!player!!.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    fun nextTrack() {
        val next = queue.poll()
        if (next != null) {
            player!!.startTrack(next, false)
        } else {
            _stop()
        }
    }

    override fun onPlayerPause(player: AudioPlayer) {
        LaylaGuildMessages.MusicMessages(channel).pauseMusic()
    }

    override fun onPlayerResume(player: AudioPlayer) {
        LaylaGuildMessages.MusicMessages(channel).unPauseMusic()
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        LaylaGuildMessages.MusicMessages(channel).startedMusic(track.info.title)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason == AudioTrackEndReason.LOAD_FAILED) {
            LaylaGuildMessages.MusicMessages(channel).loadMusicFailed(track.info.title)
            nextTrack()
        } else if (endReason.mayStartNext) {
            LaylaGuildMessages.MusicMessages(channel).finishedMusic(track.info.title)
            nextTrack()
        }
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }


}