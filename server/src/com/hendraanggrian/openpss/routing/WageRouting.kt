package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.data.Wage
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Wages
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update

object WageRouting : OpenPssRouting({
    route(Wages.schemaName) {
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
                    Wages { wageId.equal(call.getInt("wageId")) }.singleOrNull() ?: Wage.NOT_FOUND
                })
            }
            put {
                val wage = call.receive<Wage>()
                transaction {
                    Wages { wageId.equal(wage.wageId) }
                        .projection { daily + hourlyOvertime }
                        .update(wage.daily, wage.hourlyOvertime)
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
})