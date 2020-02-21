package dev.walk.gs.layla.commands

import com.google.api.services.youtube.model.SearchResult
import dev.walk.gs.layla.YTSearch
import dev.walk.gs.layla.events.CommandManager
import dev.walk.gs.layla.events.ReactionEvent
import dev.walk.gs.layla.manager.ServerManager
import dev.walk.gs.layla.manager.infix.*
import dev.walk.gs.layla.manager.layla.LaylaGuildMessages
import dev.walk.gs.layla.manager.layla.LaylaTitles
import dev.walk.gs.layla.manager.music.ConnectionStatus
import dev.walk.gs.layla.manager.music.TrackManager
import dev.walk.gs.layla.manager.music.WMusicManager
import dev.walk.gs.layla.manager.music.searchs.urlPrepose
import dev.walk.gs.layla.utils.EmoteUnicodes.Companion.unicodeNumber
import dev.walk.gs.layla.utils.MessageID
import dev.walk.gs.layla.utils.ParseType
import dev.walk.gs.layla.utils.join
import dev.walk.gs.layla.utils.parse
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.TimeUnit

class MusicCommand {

    companion object {
        fun checkCommandRoom(user: User, channel: TextChannel, guildID: String, guild: Guild) : Boolean {

            var manager = ServerManager(guildID)
            var channelID: String? = if (manager.hasDefinedData("MusicChannel")) manager.getData("MusicChannel") else null
            if (channelID != null && guild.getTextChannelById(channelID) != null) {
                if (channelID != channel.id) {
                    channel message "${user.asMention} não é permitido utilizar os comandos de música neste canal." complete 0 deleteMsg 5
                    return true
                }
            } else {
                var channelID: String? = if (manager.hasDefinedData("CommandChannel")) manager.getData("CommandChannel") else null
                if (channelID != null && guild.getTextChannelById(channelID) != null && channelID != channel.id) {
                    channel message "${user.asMention} não é permitido utilizar meus comandos neste canal." complete 0 deleteMsg 5
                    return true
                }
            }
            return false

        }
    }

    class PlayCommand : CommandManager() {

        fun sendMusicMessage(member: Member) {
            var arguments = args.join(1, " ")
            if (arguments.isNotEmpty()) {
                if (arguments.startsWith("http://", true) || arguments.startsWith("https://", true)) {
                    if (arguments.contains("list") && arguments.contains("start_radio=")) {
                        LaylaGuildMessages.MusicMessages(channel!!).loadPlayList()
                    }
                    TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).addTrack(member, arguments, false)
                } else {
                    var result = YTSearch.searchVideo(arguments, 9)
                    var index = 1
                    var clicked = false
                    var endSh = false
                    var proposes: MutableList<SearchResult> = mutableListOf()
                    channel!! message {
                        var msg = embed {
                            this thumbnail layla!!.avatarUrl
                            if (result.hasNext()) {
                                "Resultados de '$arguments': :v:" title null
                                "Selecione a música que você deseja reproduzir.\n" description false
                                while (result.hasNext()) {
                                    var video = result.next()
                                    var snippet = video.snippet
                                    if (arguments == snippet.title) {
                                        TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).addTrack(member, video.id.urlPrepose(), false)
                                        endSh = true
                                        break
                                    } else {
                                        "**$index** - ${snippet.title}" description false
                                        proposes.add((index - 1), video)
                                        index += 1
                                    }
                                }
                                "\nPara selecionar você deve reagir de acordo com a numeração da música que você deseja escutar." description false
                                "Caso você não selecione alguma música em até 30 segundos será cancelada essa ação." description false
                                "Deseja __cancelar__ esta ação? reaja com ❌ para cancelar." description false
                            } else {
                                "Nenhum resultado encontrado para '$arguments' :anguished:" title null
                            }
                        }
                        if (!endSh) {
                            var msg = msg complete 0
                            var registreds: Long = 0
                            msg!! reactions {
                                custom_emote = true
                                owner = user!!.id
                                proposes.forEachIndexed { index, s ->
                                    unicodeNumber(index + 1)!! action object : ReactionEvent() {
                                        override fun onRegister(emote: String) {
                                            registreds += 1
                                        }

                                        override fun onAddReaction(user: User) {
                                            if (!clicked) {
                                                clicked = true
                                                TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).addTrack(member, s.id.urlPrepose(), false)
                                                msg deleteMsg (11 - registreds) + 2
                                            }
                                        }

                                    }
                                }
                            }
                            msg reactions {
                                "❌" action object : ReactionEvent() {
                                    override fun onRegister(emote: String) {
                                        registreds += 1
                                    }

                                    override fun onAddReaction(user: User) {
                                        if (!clicked) {
                                            clicked = true
                                            msg deleteMsg (11 - registreds) + 2
                                            LaylaGuildMessages.MusicMessages(channel!!).cancelSelectMusic(user)
                                        }
                                    }
                                }
                            }
                            msg.delete().queueAfter(((11 - registreds) + 30), TimeUnit.SECONDS){
                                LaylaGuildMessages.MusicMessages(channel!!).cancelSelectMusic(user!!)
                            }
                        }
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).musicArgumentError(member.user)
            }
        }

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var wmusic = WMusicManager.get(guild!!)
            var member = event!!.member
            if (wmusic == null || !WMusicManager.hasConnected(guild!!)) {
                wmusic = WMusicManager(event?.channel!!, guild!!, event?.member!!, layla!!)
            }
            wmusic.connect(member!!, object : ConnectionStatus() {
                override fun onSuccess() {
                    MessageID("GChannel-$guildID").clearMetaData()
                    sendMusicMessage(member)
                }

                override fun alreadyConnected() {
                    sendMusicMessage(member)
                }
            })
        }

    }

    class JoinCommand : CommandManager() {

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var wmusic = WMusicManager.get(guild!!)
            var member = event!!.member
            if (wmusic == null) {
                wmusic = WMusicManager(event?.channel!!, guild!!, event?.member!!, layla!!)
            }
            wmusic.connect(member!!, object : ConnectionStatus() {
                override fun onSuccess() {
                    MessageID("GChannel-$guildID").clearMetaData()
                }

                override fun alreadyConnected() {
                    LaylaGuildMessages.MusicMessages(channel!!).alreadyConnected(user!!)
                }
            })
        }
    }

    class StopCommand : CommandManager() {

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var member = event!!.member
            if (WMusicManager.hasConnected(guild!!)) {
                if (WMusicManager.inMyChannel(channel!!, member!!, guild!!.audioManager)) {
                    if (member.hasPermission(Permission.ADMINISTRATOR)) {
                        LaylaGuildMessages.MusicMessages(channel!!).stopMusic()
                        TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).stopMusic(member)
                        TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).desconnect()
                    } else {
                        sendStopMusic()
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).iNotConnected(member!!.user)
            }
        }

        fun sendStopMusic() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            channel!! message {
                var vch = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).getMusicChannel(guild!!.audioManager)
                var msg = embed {
                    LaylaTitles.MUSIC.title title null
                    "\u23F9 **Votações para parar a música:** [ 0/${Math.round((vch!!.members.size / 2.0))} ]" description false
                } complete 0
                msg reactions {
                    one_execute = true
                    "\u23F9" action object : ReactionEvent() {

                        override fun onAddReaction(member: Member) {
                            if (WMusicManager.inMyChannel(channel!!, member, guild!!.audioManager)) {
                                var mid = MessageID("GChannel-$guildID")
                                var meta = mid.getMetaData("SVotes")
                                var votes = 1
                                if (meta != null) {
                                    votes = (meta as Int) + 1
                                }
                                var vch = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).getMusicChannel(guild!!.audioManager)
                                var mbs = Math.round((vch!!.members.size / 2.0))
                                if (votes >= mbs) {
                                    TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).stopMusic(member)
                                    msg deleteMsg 2
                                    mid.setMetaData("SVotes", 0)
                                    LaylaGuildMessages.MusicMessages(channel!!).voteFinish("de parar a música")
                                    LaylaGuildMessages.MusicMessages(channel!!).stopMusic()
                                    TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).desconnect()
                                } else {
                                    mid.setMetaData("SVotes", votes)
                                    msg edit embed {
                                        LaylaTitles.MUSIC.title title null
                                        "\u23F9 **Votações para parar a música:** [ $votes/$mbs ]" description false
                                    }.builder.build() queue 0
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    class NextCommand : CommandManager() {

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var member = event!!.member
            if (WMusicManager.hasConnected(guild!!)) {
                if (WMusicManager.inMyChannel(channel!!, member!!, guild!!.audioManager)) {
                    if (member.hasPermission(Permission.ADMINISTRATOR)) {
                        LaylaGuildMessages.MusicMessages(channel!!).nextMusic()
                        TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).nextTrack(member)
                    } else {
                        sendNextMusic()
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).iNotConnected(member!!.user)
            }
        }

        fun sendNextMusic() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            channel!! message {
                var vch = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).getMusicChannel(guild!!.audioManager)
                var msg = embed {
                    LaylaTitles.MUSIC.title title null
                    "⏭ **Votações para pular a música:** [ 0/${Math.round((vch!!.members.size / 2.0))} ]" description false
                } complete 0
                msg reactions {
                    one_execute = true
                    "⏭" action object : ReactionEvent() {

                        override fun onAddReaction(member: Member) {
                            if (WMusicManager.inMyChannel(channel!!, member, guild!!.audioManager)) {
                                var mid = MessageID("GChannel-$guildID")
                                var meta = mid.getMetaData("NVotes")
                                var votes = 1
                                if (meta != null) {
                                    votes = (meta as Int) + 1
                                }
                                var vch = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).getMusicChannel(guild!!.audioManager)
                                var mbs = Math.round((vch!!.members.size / 2.0))
                                if (votes >= mbs) {
                                    TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).nextTrack(member)
                                    msg deleteMsg 2
                                    mid.setMetaData("NVotes", 0)
                                    LaylaGuildMessages.MusicMessages(channel!!).voteFinish("para pular a música")
                                    LaylaGuildMessages.MusicMessages(channel!!).nextMusic()
                                } else {
                                    mid.setMetaData("NVotes", votes)
                                    msg edit embed {
                                        LaylaTitles.MUSIC.title title null
                                        "⏭ **Votações para pular a música:** [ $votes/$mbs ]" description false
                                    }.builder.build() queue 0
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    class VolumeCommand : CommandManager() {

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var member = event!!.member
            if (WMusicManager.hasConnected(guild!!)) {
                if (WMusicManager.inMyChannel(channel!!, member!!, guild!!.audioManager)) {
                    if (args.isNotEmpty()) {
                        var volume = parse(ParseType.LONG, args[1]).one.toInt()
                        if (volume in 0..100) {
                            TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).setVolume(member, volume)
                            LaylaGuildMessages.MusicMessages(channel!!).volumeChanged(member.user, volume)
                        } else {
                            LaylaGuildMessages.MusicMessages(channel!!).volumeIsInvalid(member.user)
                        }
                    } else {
                        LaylaGuildMessages.MusicMessages(channel!!).volumeIsInvalid(member.user)
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).iNotConnected(member!!.user)
            }
        }

    }

    class PauseCommand : CommandManager() {

        fun sendPause() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            channel!! message {
                var vch = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).getMusicChannel(guild!!.audioManager)
                var msg = embed {
                    LaylaTitles.MUSIC.title title null
                    "\u23F8 **Votações para pausar a música:** [ 0/${Math.round((vch!!.members.size / 2.0))} ]" description false
                } complete 0
                msg reactions {
                    one_execute = true
                    "\u23F8" action object : ReactionEvent() {

                        override fun onAddReaction(member: Member) {
                            if (WMusicManager.inMyChannel(channel!!, member, guild!!.audioManager)) {
                                var mid = MessageID("GChannel-$guildID")
                                var meta = mid.getMetaData("PVotes")
                                var votes = 1
                                if (meta != null) {
                                    votes = (meta as Int) + 1
                                }
                                var vch = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).getMusicChannel(guild!!.audioManager)
                                var mbs = Math.round((vch!!.members.size / 2.0))
                                if (votes >= mbs) {
                                    TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).pauseMusic(member)
                                    msg deleteMsg 2
                                    mid.setMetaData("PVotes", 0)
                                    LaylaGuildMessages.MusicMessages(channel!!).voteFinish("para pausar a música")
                                } else {
                                    mid.setMetaData("PVotes", votes)
                                    msg edit embed {
                                        LaylaTitles.MUSIC.title title null
                                        "\u23F8 **Votações para pausar a música:** [ $votes/$mbs ]" description false
                                    }.builder.build() queue 0
                                }
                            }
                        }

                    }
                }
            }
        }

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var member = event!!.member
            if (WMusicManager.hasConnected(guild!!)) {
                if (WMusicManager.inMyChannel(channel!!, member!!, guild!!.audioManager)) {
                    var tm = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel)
                    if (!tm.isPaused()) {
                        if (member.hasPermission(Permission.ADMINISTRATOR)) {
                            TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel).pauseMusic(member)
                        } else {
                            sendPause()
                        }
                    } else {
                        LaylaGuildMessages.MusicMessages(channel!!).alreadyPaused(user!!)
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).iNotConnected(member!!.user)
            }
        }

    }


    class unPauseCommand : CommandManager() {

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var member = event!!.member
            if (WMusicManager.hasConnected(guild!!)) {
                if (WMusicManager.inMyChannel(channel!!, member!!, guild!!.audioManager)) {
                    var tm = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel)
                    if (tm.isPaused()) {
                        tm.unPauseMusic(member)
                    } else {
                        LaylaGuildMessages.MusicMessages(channel!!).notPaused(user!!)
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).iNotConnected(member!!.user)
            }
        }

    }

    class PlayListCommand : CommandManager() {

        override fun onCommand() {
            if (checkCommandRoom(user!!, channel!!, guildID, guild!!)) { return }
            var member = event!!.member
            if (WMusicManager.hasConnected(guild!!)) {
                if (WMusicManager.inMyChannel(channel!!, member!!, guild!!.audioManager)) {
                    channel!! message {
                        embed {
                            LaylaTitles.MUSIC.title title null
                            var tm = TrackManager.selectShard(layla!!).selectGuild(guild!!).selectChannel(event!!.channel)
                            var list = tm.getTracks()
                            var thumb = tm.getTrack()
                            this thumbnail layla!!.avatarUrl
                            "**Atualmente está tocando:** ${if (thumb != null) thumb.info.title else "Nenhuma..."}" description false
                            "**Lista de músicas adiciondas:**" description false
                            list.forEachIndexed { index, track ->
                                "**${index + 1}** - ${track.info.title}" description false
                            }
                        } queue 0
                    }
                }
            } else {
                LaylaGuildMessages.MusicMessages(channel!!).iNotConnected(member!!.user)
            }
        }

    }
}