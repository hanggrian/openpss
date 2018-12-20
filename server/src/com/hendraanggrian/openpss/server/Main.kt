package com.hendraanggrian.openpss.server

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.server.route.AuthRoute
import com.hendraanggrian.openpss.server.route.CustomerRoute
import com.hendraanggrian.openpss.server.route.DateTimeRoute
import com.hendraanggrian.openpss.server.route.DigitalPriceRoute
import com.hendraanggrian.openpss.server.route.EmployeeRoute
import com.hendraanggrian.openpss.server.route.GlobalSettingRoute
import com.hendraanggrian.openpss.server.route.InvoiceRoute
import com.hendraanggrian.openpss.server.route.LogRoute
import com.hendraanggrian.openpss.server.route.OffsetPriceRoute
import com.hendraanggrian.openpss.server.route.PaymentRoute
import com.hendraanggrian.openpss.server.route.PlatePriceRoute
import com.hendraanggrian.openpss.server.route.RecessRoute
import com.hendraanggrian.openpss.server.route.WageRoute
import com.hendraanggrian.openpss.server.route.installRoutes
import com.hendraanggrian.openpss.util.jodaTimeSupport
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
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
                GsonConverter(GsonBuilder().jodaTimeSupport().create())
            )
            setPrettyPrinting()
        }
    }
    installRoutes(
        AuthRoute,
        CustomerRoute,
        DateTimeRoute,
        GlobalSettingRoute,
        InvoiceRoute,
        LogRoute,
        PlatePriceRoute,
        OffsetPriceRoute,
        DigitalPriceRoute,
        EmployeeRoute,
        PaymentRoute,
        RecessRoute,
        WageRoute
    )
}