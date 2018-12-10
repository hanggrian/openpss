package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.nosql.equal

object RecessRouting : Routing("recesses") {
    override fun Route.onInvoke() {
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