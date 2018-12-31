package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Setting
import com.hendraanggrian.openpss.schema.Settings
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

interface SettingApi : Api {

    suspend fun getSetting(key: CharSequence): Setting = client.get {
        apiUrl("${Settings.schemaName}/$key")
    }

    suspend fun setSetting(key: CharSequence, value: CharSequence): Boolean =
        client.requestStatus(HttpMethod.Post) {
            apiUrl("${Settings.schemaName}/$key")
            jsonBody(Setting(key.toString(), value.toString()))
        }
}