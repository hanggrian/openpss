package com.hendraanggrian.openpss.api.route

import com.hendraanggrian.openpss.api.Route
import com.hendraanggrian.openpss.db.schemas.Recess
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import org.joda.time.LocalTime

interface RecessRoute : Route {

    suspend fun getRecesses(): List<Recess> = client.get {
        apiUrl("recesses")
    }

    suspend fun addRecess(start: LocalTime, end: LocalTime): Recess = client.post {
        apiUrl("recesses")
        parameters(
            "start" to start,
            "end" to end
        )
    }

    suspend fun deleteRecess(start: LocalTime, end: LocalTime): Boolean = client.requestStatus {
        apiUrl("recesses")
        method = HttpMethod.Delete
        parameters(
            "start" to start,
            "end" to end
        )
    }
}