package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.api.Page
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlin.math.ceil

fun Routing.routeLog() {
    get("/logs") {
        val page = call.parameters["page"]!!.toInt()
        val count = call.parameters["count"]!!.toInt()
        val logs = transaction { Logs() }
        call.respond(
            Page(
                ceil(logs.count() / count.toDouble()).toInt(),
                logs.skip(count * page).take(count).toList()
            )
        )
    }
}