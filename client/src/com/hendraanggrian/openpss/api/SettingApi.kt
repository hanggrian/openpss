package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Setting
import com.hendraanggrian.openpss.schema.Settings
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

interface SettingApi : Api {

    suspend fun getSetting(key: String): Setting = client.get {
        apiUrl("${Settings.schemaName}/$key")
    }

    suspend fun setSetting(key: String, value: String): Boolean =
        client.requestStatus(HttpMethod.Post) {
            apiUrl("${Settings.schemaName}/$key")
            jsonBody(Setting(key, value))
        }
}