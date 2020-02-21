package dev.walk.gs.layla.manager.music.searchs

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.ResourceId
import com.google.api.services.youtube.model.SearchResult

class Youtube {

    companion object {
        val urlDefaultYt = "https://www.youtube.com/watch?v="
        val searchInit = "id,snippet"
        val searchType = "video"
        val searchFields = "items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)"
    }

    lateinit var youtube: YouTube
    lateinit var keyS: String

    fun loadHandler(appName: String, key: String) {
        keyS = key
        youtube = YouTube.Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer {})
                .setApplicationName(appName).build()
    }

    fun searchVideo(video: String, limit: Int = 3): MutableIterator<SearchResult> {
        val search = youtube.search().list(searchInit).apply {
            key = keyS
            q = video
            type = searchType
            fields = searchFields
            maxResults = limit.toLong()
        }
        val body = search.execute()
        return body.items.iterator()
    }
}

fun ResourceId.urlPrepose() = "${Youtube.urlDefaultYt}$videoId"