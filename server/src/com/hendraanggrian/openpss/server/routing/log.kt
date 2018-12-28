package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.server.getInt
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlin.math.ceil

fun Routing.logRouting() {
    get("$Logs") {
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