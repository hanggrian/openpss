package com.hendraanggrian.openpss.server

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.server.routing.authRouting
import com.hendraanggrian.openpss.server.routing.customerRouting
import com.hendraanggrian.openpss.server.routing.dateTimeRouting
import com.hendraanggrian.openpss.server.routing.digitalPriceRouting
import com.hendraanggrian.openpss.server.routing.employeeRouting
import com.hendraanggrian.openpss.server.routing.globalSettingRouting
import com.hendraanggrian.openpss.server.routing.invoiceRouting
import com.hendraanggrian.openpss.server.routing.logRouting
import com.hendraanggrian.openpss.server.routing.offsetPriceRouting
import com.hendraanggrian.openpss.server.routing.paymentRouting
import com.hendraanggrian.openpss.server.routing.platePriceRouting
import com.hendraanggrian.openpss.server.routing.recessRouting
import com.hendraanggrian.openpss.server.routing.wageRouting
import com.hendraanggrian.openpss.util.jodaTimeSerializers
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
                GsonConverter(GsonBuilder().jodaTimeSerializers().create())
            )
            if (BuildConfig.DEBUG) {
                setPrettyPrinting()
            }
        }
    }
    routing {
        authRouting()
        customerRouting()
        dateTimeRouting()
        globalSettingRouting()
        invoiceRouting()
        logRouting()
        platePriceRouting()
        offsetPriceRouting()
        digitalPriceRouting()
        employeeRouting()
        paymentRouting()
        recessRouting()
        wageRouting()
    }
}