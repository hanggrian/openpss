package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import kotlin.math.ceil

object LogRoute : Route({
    "logs" {
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
})