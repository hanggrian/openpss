package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.nosql.dbDate
import com.hendraanggrian.openpss.nosql.dbDateTime
import com.hendraanggrian.openpss.nosql.dbTime
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get

object DateTimeRouting : OpenPssRouting({
    get("date") {
        call.respond(dbDate())
    }
    get("time") {
        call.respond(dbTime())
    }
    get("date-time") {
        call.respond(dbDateTime())
    }
})