package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.GlobalSetting
import com.hendraanggrian.openpss.schema.GlobalSettings
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

interface GlobalSettingApi : Api {

    suspend fun getGlobalSetting(key: String): GlobalSetting = client.get {
        apiUrl("$GlobalSettings/$key")
    }

    suspend fun setGlobalSetting(key: String, value: String): Boolean = client.requestStatus(HttpMethod.Post) {
        apiUrl("$GlobalSettings/$key")
        parameters("value" to value)
    }
}