package dev.walk.gs.layla.manager

import dev.walk.gs.layla.Table_Name
import dev.walk.gs.layla.sheetDB

class ServerManager(id: String) {

    var query = sheetDB!!.query(Table_Name, id)

    fun setData(data: String, value: Any) {
        if (query.get(data).isNull(data)) {
            query.insert(data).setValue(value).close()
        } else {
            query.update(data).setValue(value).close()
        }
    }

    fun getData(get: String): String? {
        var getter = query.get(get)
        if (!getter.isNull(get)) {
            return getter.getString(get)
        }
        return null
    }

    fun hasDefinedData(data: String): Boolean {
        return getData(data) != null
    }

}