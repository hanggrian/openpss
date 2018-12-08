package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.server.db.nextNo
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.server.util.getBoolean
import com.hendraanggrian.openpss.server.util.getInt
import com.hendraanggrian.openpss.server.util.getString
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
    route("customers") {
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
            val customer = Customer.new(Customers.nextNo(), call.getString("name"), call.getBoolean("isCompany"))
            when {
                transaction { Customers { it.name.matches("^$customer$", Pattern.CASE_INSENSITIVE) }.isNotEmpty() } ->
                    call.respond(HttpStatusCode.NotAcceptable, "Name taken")
                else -> {
                    customer.id = transaction { Customers.insert(customer) }
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