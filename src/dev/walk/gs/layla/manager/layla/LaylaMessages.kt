package dev.walk.gs.layla.manager.layla

import dev.walk.gs.layla.manager.infix.deleteMsg
import dev.walk.gs.layla.manager.infix.message
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

enum class LaylaTitles(var title: String) {
    MUSIC("\uD83C\uDFB5 Layla - Música"),
    ERROR("🚫 Ocorreu um erro ao efetuar esta ação, motivo:"),
    CONFIGSUCESS("✅ Configuração definida com sucesso."),
    ADMIN("\uD83C\uDFB5 Layla - Admin"),
    KICK("\uD83D\uDC62 %owner deseja chutar %user?"),
    KICKED("\uD83D\uDC62 %owner chutou %user."),
    NOT_KICKED("\uD83D\uDE36 %owner não foi possível chutar %user.")
}

class LaylaGuildMessages() {

    class AdminMessages(var channel: TextChannel){

        fun kickedUser(author: Member, member: Member, motive: String){
            channel!! message {
                embed {
                    LaylaTitles.KICKED.title.replace("%owner", "${author.effectiveName}").replace("%user", "${member.effectiveName}") title null
                    "Motivo: $motive" description false
                } complete 0 deleteMsg 5
            }
        }

        fun notKick(author: Member, member: Member){
            channel!! message {
                embed {
                    LaylaTitles.NOT_KICKED.title.replace("%owner", "${author.effectiveName}").replace("%user", "${member.effectiveName}") title null
                } complete 0 deleteMsg 5
            }
        }

        fun kickArgument(user: User) {
            channel message {
                embed {
                    LaylaTitles.ADMIN.title title null
                    "❎ ${user.asMention} **argumento inválido, utilize: l!chutar <membro> <motivo>.**" description false
                } complete 0 deleteMsg 8
            }
        }

    }

    class MusicMessages(var channel: TextChannel) {

        fun addPlayList() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "✅ **Playlist carregada e adicionada.**" description false
                } queue 0
            }
        }

        fun loadPlayList() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\uD83C\uDF00 **Carregando playlist, aguarde...**" description false
                } complete 10
            }
        }

        fun addSimple(title: String) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "✅ **Adicionado:** $title" description false
                } queue 0
            }
        }

        fun startedMusic(title: String) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\uD83D\uDCA0 **Tocando agora:** $title" description false
                } queue 0
            }
        }

        fun loadMusicFailed(title: String) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\uD83D\uDD07 **Falha ao carregar a música:** $title" description false
                } queue 0
            }
        }

        fun nextMusic() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "⏭ **Música pulada.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun stopMusic() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\u23F9 **Música parada, desconectando...**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun voteFinish(votation: String) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "✍ **Votação $votation encerrada.**" description false
                } complete 0 deleteMsg 5
            }
        }

        fun finishedMusic(title: String) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\uD83D\uDCA0 **Terminou:** $title" description false
                } queue 0
            }
        }

        fun channelConected(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\uD83D\uDCA0 ${user.asMention} **conectada, pronto para tocar música para você** \uD83D\uDE18" description false
                } complete 0 deleteMsg 8
            }
        }

        fun notPermissionConnect() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ **Não tenho permissão para conectar-me neste canal.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun notConected(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **conecte-se em um canal de voz para utlizar esta função.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun notConectedMyChannel(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **conecte-se no canal de voz em que estou para utlizar esta função.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun iNotConnected(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **não estou conectada a um canal de voz para você utlizar esta função.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun pauseMusic() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "\u23F8 **Música atual pausada, para retomar utilize: __l!despausar__.**" description false
                } queue 0
            }
        }

        fun unPauseMusic() {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "▶ **Música atual retomada, para pausar utilize: __l!pausar__.**" description false
                } queue 0
            }
        }

        fun notPaused(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **a música não está pausada.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun alreadyPaused(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **a música já está pausada.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun volumeIsInvalid(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **você só pode definir o volume entre __<`0 à 10`>__.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun volumeChanged(user: User, volume: Int) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "✅ ${user.asMention} **volume alterado, para [ $volume/100 ] .**" description false
                } queue 0
            }
        }

        fun alreadyConnected(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **eu já estou conectada a um canal de voz.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun musicArgumentError(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "❎ ${user.asMention} **argumento inválido, para adicionar uma música utilize: l!play <link/titulo/nome>.**" description false
                } complete 0 deleteMsg 8
            }
        }

        fun cancelSelectMusic(user: User) {
            channel message {
                embed {
                    LaylaTitles.MUSIC.title title null
                    "✍ ${user.asMention} **seleção de música cancelada.**" description false
                } complete 0 deleteMsg 8
            }
        }

    }

}