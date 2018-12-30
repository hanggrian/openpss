package com.hendraanggrian.openpss

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.content.GsonBuilders
import com.hendraanggrian.openpss.nosql.Database
import com.hendraanggrian.openpss.routing.AuthRouting
import com.hendraanggrian.openpss.routing.CustomerRouting
import com.hendraanggrian.openpss.routing.DateTimeRouting
import com.hendraanggrian.openpss.routing.DigitalPriceRouting
import com.hendraanggrian.openpss.routing.EmployeeRouting
import com.hendraanggrian.openpss.routing.GlobalSettingRouting
import com.hendraanggrian.openpss.routing.InvoiceRouting
import com.hendraanggrian.openpss.routing.LogRouting
import com.hendraanggrian.openpss.routing.OffsetPriceRouting
import com.hendraanggrian.openpss.routing.PaymentRouting
import com.hendraanggrian.openpss.routing.PlatePriceRouting
import com.hendraanggrian.openpss.routing.RecessRouting
import com.hendraanggrian.openpss.routing.WageRouting
import com.hendraanggrian.openpss.routing.route
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

object OpenPSSServer {

    @JvmStatic fun main(args: Array<String>) {
        Database.connect()
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
            route(AuthRouting)
            route(CustomerRouting)
            route(DateTimeRouting)
            route(GlobalSettingRouting)
            route(InvoiceRouting)
            route(LogRouting)
            route(PlatePriceRouting)
            route(OffsetPriceRouting)
            route(DigitalPriceRouting)
            route(EmployeeRouting)
            route(PaymentRouting)
            route(RecessRouting)
            route(WageRouting)
        }
    }
}