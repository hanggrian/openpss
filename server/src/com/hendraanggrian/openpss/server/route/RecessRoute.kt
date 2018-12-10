package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.server.util.getLocalTime
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.nosql.equal

fun Routing.routeRecess() {
    route("recesses") {
        get {
            call.respond(transaction { Recesses() })
        }
        post {
            val recess = Recess(call.getLocalTime("start"), call.getLocalTime("end"))
            recess.id = transaction { Recesses.insert(recess) }
            call.respond(recess)
        }
        delete {
            transaction {
                Recesses -= Recesses.buildQuery {
                    and(it.start.equal(call.getLocalTime("start")))
                    and(it.start.equal(call.getLocalTime("end")))
                }.first()
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}