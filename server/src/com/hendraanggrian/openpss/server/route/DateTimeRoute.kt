package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.server.dbDate
import com.hendraanggrian.openpss.server.dbDateTime
import com.hendraanggrian.openpss.server.dbTime
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get

object DateTimeRoute : Route({
    "date" {
        get {
            call.respond(dbDate())
        }
    }
    "time" {
        get {
            call.respond(dbTime())
        }
    }
    "date-time" {
        get {
            call.respond(dbDateTime())
        }
    }
})