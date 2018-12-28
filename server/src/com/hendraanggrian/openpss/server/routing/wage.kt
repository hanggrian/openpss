package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.data.Wage
import com.hendraanggrian.openpss.schema.Wages
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update

fun Routing.wageRouting() {
    route("wages") {
        get {
            call.respond(transaction { Wages().toList() })
        }
        post {
            val wage = call.receive<Wage>()
            wage.id = transaction { Wages.insert(wage) }
            call.respond(wage)
        }
        route("{wageId}") {
            get {
                call.respond(transaction {
                    Wages { wageId.equal(call.getInt("wageId")) }.singleOrNull()
                } ?: HttpStatusCode.NotFound)
            }
            put {
                transaction {
                    Wages { wageId.equal(call.getInt("wageId")) }
                        .projection { daily + hourlyOvertime }
                        .update(call.getInt("daily"), call.getInt("hourlyOvertime"))
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}