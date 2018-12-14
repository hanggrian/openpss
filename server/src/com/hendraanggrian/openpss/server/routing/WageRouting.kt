package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.schemas.Wage
import com.hendraanggrian.openpss.db.schemas.Wages
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import kotlinx.nosql.equal
import kotlinx.nosql.update

object WageRouting : Routing({
    "wages" {
        get {
            call.respond(transaction { Wages() })
        }
        post {
            val wage = call.receive<Wage>()
            wage.id = transaction { Wages.insert(wage) }
            call.respond(wage)
        }
        "{wageId}" {
            get {
                call.respond(transaction {
                    Wages { it.wageId.equal(call.getInt("wageId")) }.singleOrNull()
                } ?: HttpStatusCode.NotFound)
            }
            put {
                transaction {
                    Wages { it.wageId.equal(call.getInt("wageId")) }
                        .projection { daily + hourlyOvertime }
                        .update(call.getInt("daily"), call.getInt("hourlyOvertime"))
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
})