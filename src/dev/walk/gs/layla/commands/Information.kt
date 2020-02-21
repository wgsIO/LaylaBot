package dev.walk.gs.layla.commands

import carbon.walk.zking.core.util.TimeFormat
import dev.walk.gs.layla.events.CommandManager
import dev.walk.gs.layla.manager.infix.message
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Region
import net.dv8tion.jda.api.entities.ChannelType
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class InformationCommand : CommandManager() {

    override fun onCommand() {

        var icon = guild!!.iconUrl
        if (icon == null) {
            icon = layla!!.avatarUrl
        }
        var channels = guild!!.channels
        var categories = guild!!.categories
        var texts = 0; var voice = 0; var on = 0; var aus = 0; var ocup = 0; var off = 0; var perso = 0; var bots = 0
        var zone = when(guild!!.region){
            Region.BRAZIL -> "Brazil/East"
            else -> "UTC"
        }
        var created = guild!!.timeCreated.atZoneSameInstant(ZoneId.of(zone))//guild!!.timeCreated //created.atZoneSameInstant(ZoneId.of(zone))
        var members = guild!!.members
        var region = guild!!.region

        channels.iterator().forEachRemaining {
            when (it.type) {
                ChannelType.TEXT -> texts += 1
                ChannelType.VOICE -> voice += 1
            }
        }

        if (query!!.get("Joined").getValue("Joined") == null) {
            query!!.insert("Joined").setValue(TimeFormat.getCurrentTime()).close()
        }

        var time = TimeFormat.getCurrentTime() - query!!.get("Joined").getLong("Joined")
        if (time < 1) {
            time = 1
        }

        members.iterator().forEachRemaining {
            when (it.onlineStatus) {
                OnlineStatus.ONLINE -> on += 1
                OnlineStatus.IDLE -> aus += 1
                OnlineStatus.DO_NOT_DISTURB -> ocup += 1
                OnlineStatus.OFFLINE -> off += 1
                OnlineStatus.INVISIBLE -> off += 1
                OnlineStatus.UNKNOWN -> off += 1
            }
            when (it.user.isBot) {
                true -> bots += 1
                false -> perso += 1
            }
        }

        channel!! message {
            embed {
                "<:discord:614837194774937600> ${guild!!.name}" title null
                ":robot:ID:" fieldline "${guild!!.id}"
                ":computer:Shard:" fieldline "${jda!!.shardInfo.shardId}"
                ":crown:Dono:" fieldline "${guild!!.owner!!.effectiveName}"
                ":earth_americas:Região:" fieldline "${region.emoji} $region"//DD de MMM, YYYY às HH:MM
                ":speech_left: Canais: (${channels.size})" fieldline ":pencil:**Texto:** $texts  `|`  :speaking_head:**Voz:** $voice  `|`  :file_cabinet:**Categoria:** ${categories.size}"
                ":calendar_spiral:Criado em:" fieldline "${created.format(DateTimeFormatter.ofPattern("dd; MMM, yyyy; HH:mm")).replaceFirst(";", " de").replaceFirst(";", " às")}"//"${created.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))} às ${created.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}"
                ":runner:Estou aqui já faz:" field "${TimeFormat.getTimeString(time)}"
                ":busts_in_silhouette: Membros: (${members.size})" fieldline "<:online:614899338233118727>**Online: ** $on | <:ausente:614899338610475189>**Ausente: ** $aus | <:ocupado:614899338623058020>**Ocupado: ** $ocup | <:offline:614899338631446612>**Offline: ** $off \n:raising_hand:**Pessoas:** $perso\n:robot:**Robôs:** $bots"
                this thumbnail icon
                this randomColor 255
            } queue 0
        }

    }

}