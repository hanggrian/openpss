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
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ConditionalHeaders
import io.ktor.features.ContentNegotiation
import io.ktor.features.PartialContent
import io.ktor.features.StatusPages
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.error
import io.ktor.websocket.WebSockets
import org.omg.CosNaming.NamingContextPackage.NotFound
import org.slf4j.Logger
import java.util.ResourceBundle

private lateinit var log: Logger

fun main(args: Array<String>) {
    startConnection()
    log = embeddedServer(Netty, applicationEngineEnvironment {
        connector {
            host = "localhost"
            port = BuildConfig.SERVER_PORT
        }
        connector {
            host = BuildConfig.SERVER_HOST
            port = BuildConfig.SERVER_PORT
        }
        module {
            if (BuildConfig.DEBUG) {
                install(CallLogging)
            }
            install(ConditionalHeaders)
            install(Compression)
            install(PartialContent)
            install(AutoHeadResponse)
            install(WebSockets)
            install(XForwardedHeaderSupport)
            install(StatusPages) {
                exception<ServiceUnavailable> {
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }
                exception<BadRequest> {
                    call.respond(HttpStatusCode.BadRequest)
                }
                exception<Unauthorized> {
                    call.respond(HttpStatusCode.Unauthorized)
                }
                exception<NotFound> {
                    call.respond(HttpStatusCode.NotFound)
                }
                exception<SecretInvalidError> {
                    call.respond(HttpStatusCode.Forbidden)
                }
                exception<Throwable> {
                    environment.log.error(it)
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
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
    }).start(wait = true).application.log
    log.info("Welcome to ${BuildConfig.NAME} ${BuildConfig.VERSION}")
    log.info("For more information, visit ${BuildConfig.WEBSITE}")
    logger?.info("Debug mode is activated, server activities will be logged here.")
}

val logger: Logger? get() = log.takeIf { BuildConfig.DEBUG }

val resources: ResourceBundle
    get() = Language.ofFullCode(transaction {
        findGlobalSetting(Setting.KEY_LANGUAGE).value
    }).toResourcesBundle()