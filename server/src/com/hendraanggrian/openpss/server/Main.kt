package com.hendraanggrian.openpss.server

import com.hendraanggrian.openpss.server.routing.routeAuth
import com.hendraanggrian.openpss.server.routing.routeCustomer
import com.hendraanggrian.openpss.server.routing.routeLog
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        routeAuth()
        routeCustomer()
        routeLog()
    }
}