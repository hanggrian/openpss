package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.Routing
import com.hendraanggrian.openpss.Server
import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.getBooleanOrNull
import com.hendraanggrian.openpss.getInt
import com.hendraanggrian.openpss.getString
import com.hendraanggrian.openpss.getStringOrNull
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Customers
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Log
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.Payments
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlin.math.ceil
import kotlinx.nosql.equal
import kotlinx.nosql.update

object InvoicesRouting : Routing({
    route(Invoices.schemaName) {
        get {
            val search = call.getInt("search")
            val customer = call.getStringOrNull("customer")
            val type = call.getStringOrNull("type")
            val isPaid = call.getBooleanOrNull("isPaid")
            val isDone = call.getBooleanOrNull("isDone")
            val date = call.getStringOrNull("date")
            val page = call.getInt("page")
            val count = call.getInt("count")
            call.respond(
                transaction {
                    val invoices = Invoices.buildQuery {
                        when {
                            search != 0 -> and(Invoices.no.equal(search))
                            else -> {
                                if (customer != null) {
                                    and(Invoices.customerId.equal(Customers { name.equal(customer) }.single().id))
                                }
                                if (type != null) {
                                }
                                if (isPaid != null) {
                                    and(Invoices.isPaid.equal(isPaid))
                                }
                                if (isDone != null) {
                                    and(Invoices.isDone.equal(isDone))
                                }
                                if (date != null) {
                                    and(Invoices.dateTime.matches(date))
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
                Payments { invoiceId.equal(invoice.id) }.remove()
                Logs += Log.new(
                    Server.getString(R.string.invoice_delete).format(
                        invoice.no,
                        customerName
                    ),
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
                val invoice = call.receive<Invoice>()
                transaction {
                    Invoices[call.getString("id")]
                        .projection { isPrinted + isPaid + isDone }
                        .update(invoice.isPrinted, invoice.isPaid, invoice.isDone)
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
})
