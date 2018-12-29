package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.server.dbDate
import com.hendraanggrian.openpss.server.dbDateTime
import com.hendraanggrian.openpss.server.dbTime
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.routeDateTimes() {
    get("date") {
        call.respond(dbDate())
    }
    get("time") {
        call.respond(dbTime())
    }
    get("date-time") {
        call.respond(dbDateTime())
    }
}