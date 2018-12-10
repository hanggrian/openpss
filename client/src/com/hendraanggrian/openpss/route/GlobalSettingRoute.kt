package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

interface GlobalSettingRoute : Route {

    suspend fun getGlobalSetting(key: String): GlobalSetting = client.get {
        apiUrl("global-settings")
        parameters("key" to key)
    }

    suspend fun setGlobalSetting(key: String, value: String): Boolean = client.requestStatus {
        apiUrl("recesses")
        method = HttpMethod.Post
        parameters(
            "key" to key,
            "value" to value
        )
    }
}