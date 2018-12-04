package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.api.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.util.regex.Pattern
import kotlin.math.ceil

fun Routing.routeCustomer() {
    get("/customers") {
        val search = call.parameters["search"]!!
        val page = call.parameters["page"]!!.toInt()
        val count = call.parameters["count"]!!.toInt()
        val customers = transaction {
            Customers.buildQuery {
                if (search.isNotBlank()) {
                    or(it.name.matches(search, Pattern.CASE_INSENSITIVE))
                    or(it.address.matches(search, Pattern.CASE_INSENSITIVE))
                    or(it.note.matches(search, Pattern.CASE_INSENSITIVE))
                }
            }
        }
        call.respond(
            Page(
                ceil(customers.count() / count.toDouble()).toInt(),
                customers.skip(count * page).take(count).toList()
            )
        )
    }
}