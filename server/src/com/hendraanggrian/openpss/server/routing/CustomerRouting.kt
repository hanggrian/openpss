package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.server.db.nextNo
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.util.isNotEmpty
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import java.util.regex.Pattern
import kotlin.math.ceil

fun Routing.routeCustomer() {
    route("customer") {
        get {
            val search = call.parameters["search"]!!
            val page = call.parameters["page"]!!.toInt()
            val count = call.parameters["count"]!!.toInt()
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
            val customer = Customer.new(
                Customers.nextNo(),
                call.parameters["name"]!!,
                call.parameters["isCompany"]!!.toBoolean()
            )
            when {
                transaction { Customers { it.name.matches("^$customer$", Pattern.CASE_INSENSITIVE) }.isNotEmpty() } ->
                    call.respond(HttpStatusCode.NotAcceptable, "Name taken")
                else -> {
                    transaction {
                        customer.id = Customers.insert(customer)
                    }
                    call.respond(customer)
                }
            }
        }
        route("{no}") {
            put {
            }
            delete {
            }
        }
    }
}