package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.schema.GlobalSetting
import com.hendraanggrian.openpss.schema.GlobalSettings
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

interface GlobalSettingsApi : Api {

    suspend fun getSetting(key: CharSequence): GlobalSetting = client.get {
        apiUrl("${GlobalSettings.schemaName}/$key")
    }

    suspend fun setSetting(key: CharSequence, value: CharSequence): Boolean =
        client.requestStatus(HttpMethod.Post) {
            apiUrl("${GlobalSettings.schemaName}/$key")
            jsonBody(
                GlobalSetting(
                    key.toString(),
                    value.toString()
                )
            )
        }
}
