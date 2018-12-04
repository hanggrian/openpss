@file:Suppress("ClassName")

package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.api.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import java.util.regex.Pattern
import kotlin.math.ceil

@Location("/customers")
class customers(val search: String, val page: String, val count: String)

fun Routing.routeCustomer() {
    get<customers> { input ->
        val page = input.page.toInt()
        val count = input.count.toInt()
        call.respond(
            transaction {
                val customers = Customers.buildQuery {
                    if (input.search.isNotBlank()) {
                        or(it.name.matches(input.search, Pattern.CASE_INSENSITIVE))
                        or(it.address.matches(input.search, Pattern.CASE_INSENSITIVE))
                        or(it.note.matches(input.search, Pattern.CASE_INSENSITIVE))
                    }
                }
                Page(
                    ceil(customers.count() / count.toDouble()).toInt(),
                    customers.skip(count * page).take(count).toList()
                )
            }
        )
    }
}