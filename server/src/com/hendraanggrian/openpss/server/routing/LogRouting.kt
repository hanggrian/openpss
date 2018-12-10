package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlin.math.ceil

object LogRouting : Routing("logs") {

    override fun Route.onInvoke() {
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