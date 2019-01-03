package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Recess
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.Recesses
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import org.joda.time.LocalTime

interface RecessesApi : Api {

    suspend fun getRecesses(): List<Recess> = client.get {
        apiUrl(Recesses.schemaName)
    }

    suspend fun addRecess(start: LocalTime, end: LocalTime): Recess = client.post {
        apiUrl(Recesses.schemaName)
        parameters(
            "start" to start,
            "end" to end
        )
    }

    suspend fun deleteRecess(id: StringId<*>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("${Recesses.schemaName}/$id")
    }
}