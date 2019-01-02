package com.hendraanggrian.openpss

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.data.GlobalSetting
import com.hendraanggrian.openpss.nosql.startConnection
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.routing.AuthRouting
import com.hendraanggrian.openpss.routing.CustomersRouting
import com.hendraanggrian.openpss.routing.DateTimeRouting
import com.hendraanggrian.openpss.routing.DigitalPriceRouting
import com.hendraanggrian.openpss.routing.EmployeeRouting
import com.hendraanggrian.openpss.routing.GlobalSettingsRouting
import com.hendraanggrian.openpss.routing.InvoicesRouting
import com.hendraanggrian.openpss.routing.LogsRouting
import com.hendraanggrian.openpss.routing.OffsetPriceRouting
import com.hendraanggrian.openpss.routing.PaymentsRouting
import com.hendraanggrian.openpss.routing.PlatePriceRouting
import com.hendraanggrian.openpss.routing.RecessesRouting
import com.hendraanggrian.openpss.routing.WagesRouting
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

val logger: Logger? get() = log.takeIf { BuildConfig.DEBUG }

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
                route(CustomersRouting)
                route(DateTimeRouting)
                route(GlobalSettingsRouting)
                route(InvoicesRouting)
                route(LogsRouting)
                route(PlatePriceRouting)
                route(OffsetPriceRouting)
                route(DigitalPriceRouting)
                route(EmployeeRouting)
                route(PaymentsRouting)
                route(RecessesRouting)
                route(WagesRouting)
            }
        }
    }).start(wait = true).environment.log
    log.info("Welcome to ${BuildConfig.NAME} ${BuildConfig.VERSION}")
    log.info("For more information, visit ${BuildConfig.WEBSITE}")
    logger?.info("Debug mode is activated, server activities will be logged here.")
}

val resources: ResourceBundle
    get() = Language.ofFullCode(transaction {
        findGlobalSetting(GlobalSetting.KEY_LANGUAGE).value
    }).toResourcesBundle()