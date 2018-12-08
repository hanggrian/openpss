package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.server.util.getBooleanOrNull
import com.hendraanggrian.openpss.server.util.getInt
import com.hendraanggrian.openpss.server.util.getStringOrNull
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlin.math.ceil

fun Routing.routeInvoice() {
    route("invoices") {
        get {
            val search = call.getInt("search")
            val customer = call.getStringOrNull("customer")
            val isPaid = call.getBooleanOrNull("isPaid")
            val date = call.getStringOrNull("date")
            val page = call.getInt("page")
            val count = call.getInt("count")
            call.respond(
                transaction {
                    val invoices = Invoices.buildQuery {
                        when {
                            search != 0 -> and(it.no.equal(search))
                            else -> {
                                if (customer != null) {
                                    and(it.customerId.equal(Customers { it.name.equal(customer) }.single().id))
                                }
                                if (isPaid != null) {
                                    and(it.isPaid.equal(isPaid))
                                }
                                if (date != null) {
                                    and(it.dateTime.matches(date))
                                }
                            }
                        }
                    }
                    Page(
                        ceil(invoices.count() / count.toDouble()).toInt(),
                        invoices.skip(count * page).take(count).toList()
                    )
                }
            )
        }
        post {
        }
        route("{no}") {
            put {
            }
            delete {
            }
        }
    }
}