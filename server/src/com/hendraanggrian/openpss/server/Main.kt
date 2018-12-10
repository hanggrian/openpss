package com.hendraanggrian.openpss.server

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.server.route.routeAuth
import com.hendraanggrian.openpss.server.route.routeCustomer
import com.hendraanggrian.openpss.server.route.routeDigitalPrice
import com.hendraanggrian.openpss.server.route.routeEmployee
import com.hendraanggrian.openpss.server.route.routeGlobalSetting
import com.hendraanggrian.openpss.server.route.routeInvoice
import com.hendraanggrian.openpss.server.route.routeLog
import com.hendraanggrian.openpss.server.route.routeOffsetPrice
import com.hendraanggrian.openpss.server.route.routePlatePrice
import com.hendraanggrian.openpss.server.route.routeRecess
import com.hendraanggrian.openpss.util.jodaTimeSupport
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.routing.routing

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            register(
                ContentType.Application.Json,
                GsonConverter(GsonBuilder().jodaTimeSupport().create())
            )
            setPrettyPrinting()
        }
    }
    routing {
        routeAuth()

        routeCustomer()

        routeLog()

        routeGlobalSetting()

        routeInvoice()

        routePlatePrice()
        routeOffsetPrice()
        routeDigitalPrice()
        routeEmployee()

        routeRecess()
    }
}