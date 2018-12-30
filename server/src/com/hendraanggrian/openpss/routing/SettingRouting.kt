package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.data.Setting
import com.hendraanggrian.openpss.logger
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Settings
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.update

object SettingRouting : OpenPssRouting({
    route("${Settings.schemaName}/{key}") {
        get {
            call.respond(transaction {
                Settings { key.equal(call.getString("key")) }.single()
            })
        }
        post {
            val (key, value) = call.receive<Setting>()
            transaction {
                Settings { this.key.equal(call.getString("key")) }
                    .projection { this.value }
                    .update(value)
            }
            call.respond(HttpStatusCode.OK)
            logger?.info("Setting '$key' has been changed to '$value'")
        }
    }
})