package dev.walk.gs.layla.utils

import carbon.walk.zking.core.util.TimeFormat
import net.dv8tion.jda.api.entities.User

var cooldown_users: MutableMap<String, Long> = mutableMapOf()

class CooldownManager(var user: User) {

    fun createCooldown(time: Long) {
        cooldown_users[user.id] = TimeFormat.getCurrentTime() + time
    }

    fun hasCooldown(): Boolean {
        val value = cooldown_users[user.id]
        if (value != null) {
            if ((value - TimeFormat.getCurrentTime()) > 0) {
                return true
            }
        }
        return false
    }

    fun getRestantCooldown(): Long {
        if (hasCooldown()) {
            return (cooldown_users[user.id]!! - TimeFormat.getCurrentTime())
        }
        return 0
    }

}