package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.OpenPSSServer
import com.hendraanggrian.openpss.data.GlobalSetting
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.GlobalSettings
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update

object GlobalSettingsRouting : OpenPssRouting({
    route("${GlobalSettings.schemaName}/{key}") {
        get {
            call.respond(transaction {
                GlobalSettings { key.equal(call.getString("key")) }.single()
            })
        }
        post {
            val (key, value) = call.receive<GlobalSetting>()
            transaction {
                GlobalSettings { this.key.equal(call.getString("key")) }
                    .projection { this.value }
                    .update(value)
            }
            call.respond(HttpStatusCode.OK)
            OpenPSSServer.logger?.info("GlobalSetting '$key' has been changed to '$value'")
        }
    }
})