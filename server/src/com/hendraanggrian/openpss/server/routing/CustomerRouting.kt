package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.server.db.matches
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.util.isNotEmpty
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import kotlinx.nosql.update
import java.util.regex.Pattern
import kotlin.math.ceil

object CustomerRouting : Routing({
    "customers" {
        get {
            val search = call.getString("search")
            val page = call.getInt("page")
            val count = call.getInt("count")
            call.respond(
                transaction {
                    val customers = Customers.buildQuery {
                        if (search.isNotBlank()) {
                            or(it.name.matches(search, Pattern.CASE_INSENSITIVE))
                            or(it.address.matches(search, Pattern.CASE_INSENSITIVE))
                            or(it.note.matches(search, Pattern.CASE_INSENSITIVE))
                        }
                    }
                    Page(
                        ceil(customers.count() / count.toDouble()).toInt(),
                        customers.skip(count * page).take(count).toList()
                    )
                }
            )
        }
        post {
            val customer = call.receive<Customer>()
            when {
                transaction {
                    Customers {
                        it.name.matches(
                            "^$customer$",
                            Pattern.CASE_INSENSITIVE
                        )
                    }.isNotEmpty()
                } ->
                    call.respond(HttpStatusCode.NotAcceptable, "Name taken")
                else -> {
                    customer.id = transaction { Customers.insert(customer) }
                    call.respond(customer)
                }
            }
        }
        "{id}" {
            get {
                call.respond(transaction { Customers[call.getString("id")] })
            }
            put {
                transaction {
                    val query = Customers[call.getString("id")]
                    val customerName = query.single().name
                    query.projection { address + note }
                        .update(call.getString("address"), call.getString("note"))
                    Logs += Log.new(
                        InvoiceRouting.resources.getString(R.string.customer_edit).format(customerName),
                        call.getString("login")
                    )
                }
                call.respond(HttpStatusCode.OK)
            }
            "contacts" {
                post {
                    val contact = call.receive<Customer.Contact>()
                    transaction {
                        val query = Customers[call.getString("id")]
                        query.projection { contacts }
                            .update(query.single().contacts + contact)
                    }
                    call.respond(contact)
                }
                delete {
                    val contact = call.receive<Customer.Contact>()
                    transaction {
                        val query = Customers[call.getString("id")]
                        val customer = query.single()
                        query.projection { contacts }
                            .update(customer.contacts - contact)
                        Logs += Log.new(
                            InvoiceRouting.resources.getString(R.string.contact_deleted).format(
                                contact.value,
                                customer.name
                            ),
                            call.getString("login")
                        )
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
})