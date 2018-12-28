package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.GlobalSetting
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

interface GlobalSettingApi : Api {

    suspend fun getGlobalSetting(key: String): GlobalSetting = client.get {
        apiUrl("global-settings/$key")
    }

    suspend fun setGlobalSetting(key: String, value: String): Boolean = client.requestStatus(HttpMethod.Post) {
        apiUrl("global-settings/$key")
        parameters("value" to value)
    }
}