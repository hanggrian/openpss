package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.server.util.getString
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Routing.routeGlobalSetting() {
    route("global-settings") {
        get {
            val key = call.getString("key")
        }

        post {

        }
    }
}