package com.hendraanggrian.openpss.server

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.server.routing.routeAuth
import com.hendraanggrian.openpss.server.routing.routeCustomer
import com.hendraanggrian.openpss.server.routing.routeInvoice
import com.hendraanggrian.openpss.server.routing.routeLog
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.locations.Locations
import io.ktor.routing.routing

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(ContentNegotiation) {
        gson {
            register(
                ContentType.Application.Json,
                GsonConverter(GsonBuilder().apply {
                    Converters.registerLocalDate(this)
                    Converters.registerLocalTime(this)
                    Converters.registerLocalDateTime(this)
                    Converters.registerDateTime(this)
                }.create())
            )
            setPrettyPrinting()
        }
    }
    routing {
        routeAuth()
        routeCustomer()
        routeLog()
        routeInvoice()
    }
}