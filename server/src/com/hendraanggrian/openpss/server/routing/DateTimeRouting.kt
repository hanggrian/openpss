package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.Database
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get

object DateTimeRouting : Routing({
    "date" {
        get {
            call.respond(Database.date())
        }
    }
    "time" {
        get {
            call.respond(Database.time())
        }
    }
    "date-time" {
        get {
            call.respond(Database.dateTime())
        }
    }
})