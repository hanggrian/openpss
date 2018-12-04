@file:Suppress("ClassName")

package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.api.Page
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import kotlin.math.ceil

@Location("/logs")
class logs(val page: String, val count: String)

fun Routing.routeLog() {
    get<logs> { input ->
        val page = input.page.toInt()
        val count = input.count.toInt()
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