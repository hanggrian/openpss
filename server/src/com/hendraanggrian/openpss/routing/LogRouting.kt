package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Logs
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import kotlin.math.ceil

object LogRouting : Routing({
    get(Logs.schemaName) {
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
})