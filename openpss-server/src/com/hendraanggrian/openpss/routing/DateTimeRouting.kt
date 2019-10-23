package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.nosql.Database
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get

object DateTimeRouting : Routing({
    get("date") {
        call.respond(Database.date())
    }
    get("time") {
        call.respond(Database.time())
    }
    get("date-time") {
        call.respond(Database.dateTime())
    }
})
