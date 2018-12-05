@file:Suppress("ClassName")

package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import kotlinx.nosql.equal
import kotlin.math.ceil

@Location("/invoices")
data class invoices(
    val search: Int,
    val customer: String?,
    val isPaid: Boolean?,
    val date: Any?,
    val page: Int,
    val count: Int
)

fun Routing.routeInvoice() {
    get<invoices> { input ->
        call.respond(
            transaction {
                val invoices = Invoices.buildQuery {
                    when {
                        input.search != 0 -> and(it.no.equal(input.search))
                        else -> {
                            if (input.customer != null) {
                                and(it.customerId.equal(Customers { it.name.equal(input.customer) }.single().id))
                            }
                            if (input.isPaid != null) {
                                and(it.isPaid.equal(input.isPaid))
                            }
                            if (input.date != null) {
                                and(it.dateTime.matches(input.date))
                            }
                        }
                    }
                }
                Page(
                    ceil(invoices.count() / input.count.toDouble()).toInt(),
                    invoices.skip(input.count * input.page).take(input.count).toList()
                )
            }
        )
    }
}