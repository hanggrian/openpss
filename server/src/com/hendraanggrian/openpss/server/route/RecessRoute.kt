package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post

object RecessRoute : Route({
    "recesses" {
        get {
            call.respond(transaction { Recesses() })
        }
        post {
            val recess = Recess(call.getLocalTime("start"), call.getLocalTime("end"))
            recess.id = transaction { Recesses.insert(recess) }
            call.respond(recess)
        }
        "{id}" {
            delete {
                transaction {
                    Recesses -= Recesses[call.getString("id")].single()
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
})