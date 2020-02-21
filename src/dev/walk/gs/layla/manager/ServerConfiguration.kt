package dev.walk.gs.layla.manager

import dev.walk.gs.layla.utils.MultiValue
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

var users_config: MutableMap<Guild, ServerConfiguration> = mutableMapOf()
var executors: MutableMap<User, Guild> = mutableMapOf()

fun hasEditingServer(user: User): Boolean {
    return executors[user] != null
}

fun getGuildEditting(user: User): Guild? {
    return executors[user]
}

class ServerConfiguration(private var user: User) {

    companion object {
        fun get(user: User): ServerConfiguration? {
            return users_config[getGuildEditting(user)]
        }

        fun get(guild: Guild): ServerConfiguration? {
            return users_config[guild]
        }
    }

    private var data: MultiValue<Guild, String>? = null
    private var values: MultiValue<String, Any?>? = null
    private var configurations: MutableMap<String, Any?> = mutableMapOf()
    var message_id: String? = null
    var message: Message? = null
    var embed_main: Message? = null

    fun setServerConfig(guild: Guild, status: String): ServerConfiguration {
        data = MultiValue(guild, status)
        values = MultiValue(status, null)
        configurations.clear()
        users_config[data!!.one] = this
        executors[user] = guild
        return this
    }

    fun cancel(): ServerConfiguration {
        users_config.remove(data!!.one)
        executors.remove(user)
        return this
    }

    fun setStatus(status: String): ServerConfiguration {
        data = MultiValue(data!!.one, status)
        return this
    }

    fun getStatus(): String {
        return data!!.two
    }

    fun setDataValue(value: Any): ServerConfiguration {
        values = MultiValue(data!!.two, value)
        return this
    }

    fun save(): ServerConfiguration {
        configurations[data!!.two] = values!!.two
        return this
    }

    fun saveModify() {
        configurations.keys.iterator().forEachRemaining {
            ServerManager(getGuild().id).setData(it, configurations[it]!!)
        }
    }

    fun getGuild(): Guild {
        return data!!.one
    }

}