package com.hendraanggrian.openpss

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.data.Setting
import com.hendraanggrian.openpss.nosql.startConnection
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.routing.AuthRouting
import com.hendraanggrian.openpss.routing.CustomerRouting
import com.hendraanggrian.openpss.routing.DateTimeRouting
import com.hendraanggrian.openpss.routing.DigitalPriceRouting
import com.hendraanggrian.openpss.routing.EmployeeRouting
import com.hendraanggrian.openpss.routing.InvoiceRouting
import com.hendraanggrian.openpss.routing.LogRouting
import com.hendraanggrian.openpss.routing.OffsetPriceRouting
import com.hendraanggrian.openpss.routing.PaymentRouting
import com.hendraanggrian.openpss.routing.PlatePriceRouting
import com.hendraanggrian.openpss.routing.RecessRouting
import com.hendraanggrian.openpss.routing.SettingRouting
import com.hendraanggrian.openpss.routing.WageRouting
import com.hendraanggrian.openpss.routing.route
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
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
import org.slf4j.Logger
import java.util.ResourceBundle

private lateinit var log: Logger

fun main(args: Array<String>) {
    startConnection()
    log = embeddedServer(Netty, commandLineEnvironment(args)).start().application.log
    log.info("Welcome to ${BuildConfig.NAME} ${BuildConfig.VERSION}")
    log.info("For more information, visit ${BuildConfig.WEBSITE}")
    logger?.info("Debug mode is activated, server activities will be logged here.")
}

val logger: Logger? get() = log.takeIf { BuildConfig.DEBUG }

val resources: ResourceBundle
    get() = Language.ofFullCode(transaction {
        findGlobalSetting(Setting.KEY_LANGUAGE).value
    }).toResourcesBundle()

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            register(
                ContentType.Application.Json,
                GsonConverter(GsonBuilder().registerJodaTimeSerializers().create())
            )
            if (BuildConfig.DEBUG) {
                setPrettyPrinting()
            }
        }
    }
    routing {
        route(AuthRouting)
        route(CustomerRouting)
        route(DateTimeRouting)
        route(InvoiceRouting)
        route(LogRouting)
        route(PlatePriceRouting)
        route(OffsetPriceRouting)
        route(DigitalPriceRouting)
        route(EmployeeRouting)
        route(PaymentRouting)
        route(RecessRouting)
        route(SettingRouting)
        route(WageRouting)
    }
}