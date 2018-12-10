package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Log
import io.ktor.client.request.get

interface LogRoute : Route {

    suspend fun getLogs(page: Int, count: Int): Page<Log> = client.get {
        apiUrl("logs")
        parameters(
            "page" to page,
            "count" to count
        )
    }
}