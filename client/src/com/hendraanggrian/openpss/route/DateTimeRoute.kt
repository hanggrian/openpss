package com.hendraanggrian.openpss.route

import io.ktor.client.request.get
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

interface DateTimeRoute : Route {

    suspend fun getDate(): LocalDate = client.get {
        apiUrl("date")
    }

    suspend fun getTime(): LocalTime = client.get {
        apiUrl("time")
    }

    suspend fun getDateTime(): DateTime = client.get {
        apiUrl("date-time")
    }
}