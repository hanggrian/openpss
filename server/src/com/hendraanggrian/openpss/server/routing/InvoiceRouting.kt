package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlin.math.ceil

object InvoiceRouting : Routing("invoices") {

    override fun Route.onInvoke() {
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
            val invoice = call.receive<Invoice>()
            invoice.id = transaction { Invoices.insert(invoice) }
            call.respond(invoice)
        }
        route("{no}") {
            get {
                call.respond(transaction {
                    Invoices { it.no.equal(call.getInt("no")) }.single()
                })
            }
            put {
                val payment = call.getString("payment")
            }
            delete {
                val invoice = call.receive<Invoice>()
                transaction {
                    val customerName = Customers[invoice.customerId].single().name
                    Invoices -= invoice.id
                    Payments { Payments.invoiceId.equal(invoice.id) }.remove()
                    Logs += Log.new(
                        resources.getString(R.string.invoice_delete).format(invoice.no, customerName),
                        call.getString("login")
                    )
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}