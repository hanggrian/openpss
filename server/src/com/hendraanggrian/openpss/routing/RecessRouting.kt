package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.data.Recess
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Recesses
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

object RecessRouting : Routing({
    route(Recesses.schemaName) {
        get {
            call.respond(transaction { Recesses().toList() })
        }
        post {
            val recess = Recess(call.getLocalTime("start"), call.getLocalTime("end"))
            recess.id = transaction { Recesses.insert(recess) }
            call.respond(recess)
        }
        route("{id}") {
            delete {
                transaction {
                    Recesses -= Recesses[call.getString("id")].single()
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
})