package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.Log
import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.schema.Customers
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.Payments
import com.hendraanggrian.openpss.server.db.matches
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import kotlinx.nosql.equal
import kotlinx.nosql.update
import kotlin.math.ceil

object InvoiceRoute : Route({
    "invoices" {
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
                                if (isDone != null) {
                                    and(it.isDone.equal(isDone))
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
        delete {
            val invoice = call.receive<Invoice>()
            transaction {
                val customerName = Customers[invoice.customerId].single().name
                Invoices -= invoice.id
                Payments { Payments.invoiceId.equal(invoice.id) }.remove()
                Logs += Log.new(
                    InvoiceRoute.resources.getString(R.string.invoice_delete).format(invoice.no, customerName),
                    call.getString("login")
                )
            }
            call.respond(HttpStatusCode.OK)
        }
        "{id}" {
            get {
                call.respond(transaction {
                    Invoices { it.no.equal(call.getInt("id")) }.single()
                })
            }
            put {
                transaction {
                    Invoices { it.no.equal(call.getInt("id")) }
                        .projection { isPrinted + isPaid + isDone }
                        .update(call.getBoolean("isPrinted"), call.getBoolean("isPaid"), call.getBoolean("isDone"))
                }
                call.respond(HttpStatusCode.OK)
            }
        }
        "next" {
            get {
                call.respond(transaction { Invoices().lastOrNull()?.no ?: 0 } + 1)
            }
        }
    }
})