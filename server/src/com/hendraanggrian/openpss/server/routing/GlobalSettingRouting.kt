package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.nosql.equal
import kotlinx.nosql.update

object GlobalSettingRouting : Routing("global-settings") {

    override fun Route.onInvoke() {
        get {
            val key = call.getString("key")
            call.respond(transaction {
                GlobalSettings { it.key.equal(key) }.single()
            })
        }
        post {
            val key = call.getString("key")
            val value = call.getString("value")
            transaction {
                GlobalSettings { it.key.equal(key) }
                    .projection { this.value }
                    .update(value)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}