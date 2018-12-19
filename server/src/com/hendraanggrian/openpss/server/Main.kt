package com.hendraanggrian.openpss.server

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.server.routing.AuthRouting
import com.hendraanggrian.openpss.server.routing.CustomerRouting
import com.hendraanggrian.openpss.server.routing.DateTimeRouting
import com.hendraanggrian.openpss.server.routing.DigitalPriceRouting
import com.hendraanggrian.openpss.server.routing.EmployeeRouting
import com.hendraanggrian.openpss.server.routing.GlobalSettingRouting
import com.hendraanggrian.openpss.server.routing.InvoiceRouting
import com.hendraanggrian.openpss.server.routing.LogRouting
import com.hendraanggrian.openpss.server.routing.OffsetPriceRouting
import com.hendraanggrian.openpss.server.routing.PaymentRouting
import com.hendraanggrian.openpss.server.routing.PlatePriceRouting
import com.hendraanggrian.openpss.server.routing.RecessRouting
import com.hendraanggrian.openpss.server.routing.WageRouting
import com.hendraanggrian.openpss.server.routing.installRoutings
import com.hendraanggrian.openpss.util.jodaTimeSupport
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType

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
    installRoutings(
        AuthRouting,
        CustomerRouting,
        DateTimeRouting,
        GlobalSettingRouting,
        InvoiceRouting,
        LogRouting,
        PlatePriceRouting,
        OffsetPriceRouting,
        DigitalPriceRouting,
        EmployeeRouting,
        PaymentRouting,
        RecessRouting,
        WageRouting
    )
}