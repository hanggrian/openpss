package com.hendraanggrian.openpss

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.nosql.Database
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
import com.hendraanggrian.openpss.schema.GlobalSetting
import com.hendraanggrian.openpss.ui.TextDialog
import com.hendraanggrian.openpss.ui.menuItem
import com.hendraanggrian.openpss.ui.popupMenu
import com.hendraanggrian.openpss.ui.systemTray
import com.hendraanggrian.openpss.ui.trayIcon
import io.ktor.application.call
import io.ktor.application.install
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
import java.awt.Desktop
import java.awt.SystemTray
import java.net.URI
import java.util.ResourceBundle
import kotlin.system.exitProcess
import org.slf4j.Logger

object Server : StringResources {

    private var logger: Logger? = null

    fun log(message: String) {
        if (BuildConfig.DEBUG) {
            logger?.info(message)
        }
    }

    override val resourceBundle: ResourceBundle
        get() = Language.ofFullCode(transaction {
            findGlobalSetting(GlobalSetting.KEY_LANGUAGE).value
        }).toResourcesBundle()

    @JvmStatic
    fun main(@Suppress("UnusedMainParameter") args: Array<String>) {
        Database.run()
        when {
            !SystemTray.isSupported() ->
                TextDialog(this, R.string.system_tray_is_unsupported).display()
            else -> systemTray {
                trayIcon(R.icon) {
                    toolTip = buildString {
                        append("${BuildConfig.NAME} ${BuildConfig.VERSION}")
                        if (BuildConfig.DEBUG) append(" (Debug)")
                    }
                    isImageAutoSize = true
                    popupMenu {
                        menuItem(getString(R.string.about).format(toolTip)) {
                            addActionListener {
                                when {
                                    !Desktop.isDesktopSupported() -> TextDialog(
                                        this@Server,
                                        R.string.desktop_is_unsupported
                                    ).display()
                                    else -> Desktop.getDesktop().browse(URI(BuildConfig.WEBSITE))
                                }
                            }
                        }
                        addSeparator()
                        menuItem(getString(R.string.quit)) {
                            addActionListener { exitProcess(0) }
                        }
                    }
                }
            }
        }
        logger = embeddedServer(Netty, applicationEngineEnvironment {
            connector {
                host = "0.0.0.0"
                port = 8080
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
        log("Welcome to ${BuildConfig.NAME} ${BuildConfig.VERSION}")
        log("For more information, visit ${BuildConfig.WEBSITE}")
        log("Debug mode is activated, server activities will be logged here.")
    }
}
