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
data class logs(val page: Int, val count: Int)

fun Routing.routeLog() {
    get<logs> { input ->
        call.respond(
            transaction {
                val logs = Logs()
                Page(
                    ceil(logs.count() / input.count.toDouble()).toInt(),
                    logs.skip(input.count * input.page).take(input.count).toList()
                )
            }
        )
    }
}