package com.hendraanggrian.openpss.server

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.content.GsonBuilders
import com.hendraanggrian.openpss.server.route.routeAuth
import com.hendraanggrian.openpss.server.route.routeCustomers
import com.hendraanggrian.openpss.server.route.routeDateTimes
import com.hendraanggrian.openpss.server.route.routeDigitalPrices
import com.hendraanggrian.openpss.server.route.routeEmployees
import com.hendraanggrian.openpss.server.route.routeGlobalSettings
import com.hendraanggrian.openpss.server.route.routeInvoices
import com.hendraanggrian.openpss.server.route.routeLogs
import com.hendraanggrian.openpss.server.route.routeOffsetPrices
import com.hendraanggrian.openpss.server.route.routePayments
import com.hendraanggrian.openpss.server.route.routePlatePrices
import com.hendraanggrian.openpss.server.route.routeRecesses
import com.hendraanggrian.openpss.server.route.routeWages
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    connect()
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            register(
                ContentType.Application.Json,
                GsonConverter(GsonBuilders.registerJodaTime(GsonBuilder()).create())
            )
            if (BuildConfig.DEBUG) {
                setPrettyPrinting()
            }
        }
    }
    routing {
        routeAuth()
        routeCustomers()
        routeDateTimes()
        routeGlobalSettings()
        routeInvoices()
        routeLogs()
        routePlatePrices()
        routeOffsetPrices()
        routeDigitalPrices()
        routeEmployees()
        routePayments()
        routeRecesses()
        routeWages()
    }
}