package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.db.schemas.Recess
import io.ktor.client.request.get
import io.ktor.client.request.post

interface GlobalSettingRoute : Route {

    suspend fun getGlobalSetting(key: String): String = client.get {
        apiUrl("global-settings")
        parameters("key" to key)
    }

    suspend fun setGlobalSetting(key: String, value: String): Recess = client.post {
        apiUrl("recesses")
        parameters(
            "key" to key,
            "value" to value
        )
    }
}