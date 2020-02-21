package dev.walk.gs.layla.utils

import net.dv8tion.jda.api.entities.Message
import java.util.*

private val mmeta = HashMap<String, HashMap<String, Any>>()

fun Message.getMetaDataList(): HashMap<String, Any> {
    var meta: HashMap<String, Any>? = mmeta["$id"]
    if (meta == null) {
        meta = HashMap()
        mmeta["$id"] = meta
    }
    return meta
}

fun Message.getMetaData(key: String): Any? {
    val meta = getMetaDataList()
    return if (meta.containsKey(key)) {
        meta[key]
    } else null
}

fun Message.getAmmountData(): Int {
    return getMetaDataList().size
}

fun Message.containsData(key: String): Boolean {
    return getMetaDataList().containsKey(key)
}

fun Message.removeMetadata(key: String) {
    if (containsData(key)) {
        val meta = getMetaDataList()
        if (meta.containsKey(key)) {
            meta.remove(key)
        }
        mmeta.replace("$id", meta)
    }
}

fun Message.setMetaData(key: String, value: Any) {
    val meta = getMetaDataList()
    if (containsData(key)) {
        meta.replace(key, value)
    } else {
        meta[key] = value
    }
    mmeta.replace("$id", meta)
}

fun Message.clearMetaData() {
    if (mmeta.containsKey(id)) {
        mmeta[id]!!.clear()
    }
}

class MessageID(var id: String) {

    fun isMessageRegistred(): Boolean {
        return mmeta[id] != null
    }

    fun getMetaDataList(): HashMap<String, Any> {
        var meta: HashMap<String, Any>? = mmeta[id]
        if (meta == null) {
            meta = HashMap()
            mmeta[id] = meta
        }
        return meta
    }

    fun getMetaData(key: String): Any? {
        val meta = getMetaDataList()
        return if (meta.containsKey(key)) {
            meta[key]
        } else null
    }

    fun getAmmountData(): Int {
        return getMetaDataList().size
    }

    fun containsData(key: String): Boolean {
        return getMetaDataList().containsKey(key)
    }

    fun removeMetadata(key: String) {
        if (containsData(key)) {
            val meta = getMetaDataList()
            if (meta.containsKey(key)) {
                meta.remove(key)
            }
            mmeta.replace(id, meta)
        }
    }

    fun setMetaData(key: String, value: Any) {
        val meta = getMetaDataList()
        if (containsData(key)) {
            meta.replace(key, value)
        } else {
            meta[key] = value
        }
        mmeta.replace(id, meta)
    }

    fun clearMetaData() {
        if (mmeta.containsKey(id)) {
            mmeta[id]!!.clear()
        }
    }


}