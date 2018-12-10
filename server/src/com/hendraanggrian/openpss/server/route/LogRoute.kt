package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.server.util.getInt
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlin.math.ceil

fun Routing.routeLog() {
    route("logs") {
        get {
            val page = call.getInt("page")
            val count = call.getInt("count")
            call.respond(
                transaction {
                    val logs = Logs()
                    Page(
                        ceil(logs.count() / count.toDouble()).toInt(),
                        logs.skip(count * page).take(count).toList()
                    )
                }
            )
        }
    }
}