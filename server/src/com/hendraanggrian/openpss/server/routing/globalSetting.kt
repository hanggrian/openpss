package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.schema.GlobalSettings
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update

fun Routing.globalSettingRouting() {
    route("global-settings/{key}") {
        get {
            call.respond(transaction {
                GlobalSettings { it.key.equal(call.getString("key")) }.single()
            })
        }
        post {
            transaction {
                GlobalSettings { it.key.equal(call.getString("key")) }
                    .projection { this.value }
                    .update(call.getString("value"))
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}