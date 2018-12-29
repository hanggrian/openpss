package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.GlobalSettings
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update

object GlobalSettingRoute : Route({
    route("${GlobalSettings.schemaName}/{key}") {
        get {
            call.respond(transaction {
                GlobalSettings { key.equal(call.getString("key")) }.single()
            })
        }
        post {
            transaction {
                GlobalSettings { key.equal(call.getString("key")) }
                    .projection { value }
                    .update(call.getString("value"))
            }
            call.respond(HttpStatusCode.OK)
        }
    }
})