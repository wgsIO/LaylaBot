package dev.walk.gs.layla.events

import carbon.walk.zking.core.util.TimeFormat
import dev.walk.gs.layla.Table_Name
import dev.walk.gs.layla.sheetDB
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Listener : ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {
        sheetDB!!.createFeather(Table_Name, event.guild.id)
        var query = sheetDB!!.query(Table_Name, event.guild.id)
        if (query.get("Prefix").getString("Prefix") == null) {
            query.insert("Prefix").setValue("l").close()
        }
        if (query.get("Joined").getValue("Joined") == null) {
            query.insert("Joined").setValue(TimeFormat.getCurrentTime()).close()
        } else {
            query.update("Joined").setValue(TimeFormat.getCurrentTime()).close()
        }
    }

}