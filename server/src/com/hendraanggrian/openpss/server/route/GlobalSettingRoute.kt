package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.nosql.equal
import kotlinx.nosql.update

object GlobalSettingRoute : Route({
    "global-settings" {
        "{key}" {
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
})