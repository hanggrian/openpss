package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.nosql.Database
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.dateTime() {
    get("date") {
        call.respond(Database.date())
    }
    get("time") {
        call.respond(Database.time())
    }
    get("date-time") {
        call.respond(Database.dateTime())
    }
}
