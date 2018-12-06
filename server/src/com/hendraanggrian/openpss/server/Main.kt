package com.hendraanggrian.openpss.server

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.server.routing.routeAuth
import com.hendraanggrian.openpss.server.routing.routeCustomer
import com.hendraanggrian.openpss.server.routing.routeDigitalPrice
import com.hendraanggrian.openpss.server.routing.routeEmployee
import com.hendraanggrian.openpss.server.routing.routeInvoice
import com.hendraanggrian.openpss.server.routing.routeLog
import com.hendraanggrian.openpss.server.routing.routeOffsetPrice
import com.hendraanggrian.openpss.server.routing.routePlatePrice
import com.hendraanggrian.openpss.server.routing.routeRecess
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

        routeInvoice()

        routePlatePrice()
        routeOffsetPrice()
        routeDigitalPrice()
        routeEmployee()

        routeRecess()
    }
}