package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.Log
import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.schema.Customers
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.Payments
import com.hendraanggrian.openpss.server.R
import com.hendraanggrian.openpss.server.getBoolean
import com.hendraanggrian.openpss.server.getBooleanOrNull
import com.hendraanggrian.openpss.server.getInt
import com.hendraanggrian.openpss.server.getString
import com.hendraanggrian.openpss.server.getStringOrNull
import com.hendraanggrian.openpss.server.resources
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update
import kotlin.math.ceil

fun Routing.invoiceRouting() {
    route("$Invoices") {
        get {
            val search = call.getInt("search")
            val customer = call.getStringOrNull("customer")
            val isPaid = call.getBooleanOrNull("isPaid")
            val isDone = call.getBooleanOrNull("isDone")
            val date = call.getStringOrNull("date")
            val page = call.getInt("page")
            val count = call.getInt("count")
            call.respond(
                transaction {
                    val invoices = Invoices.buildQuery { and, _ ->
                        when {
                            search != 0 -> and(no.equal(search))
                            else -> {
                                if (customer != null) {
                                    and(customerId.equal(Customers { name.equal(customer) }.single().id))
                                }
                                if (isPaid != null) {
                                    and(this.isPaid.equal(isPaid))
                                }
                                if (isDone != null) {
                                    and(this.isDone.equal(isDone))
                                }
                                if (date != null) {
                                    and(dateTime.matches(date))
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
        route("{id}") {
            get {
                call.respond(transaction {
                    Invoices[call.getString("id")].single()
                })
            }
            put {
                transaction {
                    Invoices[call.getString("id")]
                        .projection { isPrinted + isPaid + isDone }
                        .update(call.getBoolean("isPrinted"), call.getBoolean("isPaid"), call.getBoolean("isDone"))
                }
                call.respond(HttpStatusCode.OK)
            }
        }
        route("next") {
            get {
                call.respond(transaction { Invoices().lastOrNull()?.no ?: 0 } + 1)
            }
        }
    }
}