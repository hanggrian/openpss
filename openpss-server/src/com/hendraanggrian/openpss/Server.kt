package com.hendraanggrian.openpss

import com.google.gson.GsonBuilder
import com.hendraanggrian.openpss.nosql.Database
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.route.auth
import com.hendraanggrian.openpss.route.customer
import com.hendraanggrian.openpss.route.dateTime
import com.hendraanggrian.openpss.route.digitalPrice
import com.hendraanggrian.openpss.route.employee
import com.hendraanggrian.openpss.route.globalSetting
import com.hendraanggrian.openpss.route.invoice
import com.hendraanggrian.openpss.route.log
import com.hendraanggrian.openpss.route.offsetPrice
import com.hendraanggrian.openpss.route.payment
import com.hendraanggrian.openpss.route.platePrice
import com.hendraanggrian.openpss.route.recess
import com.hendraanggrian.openpss.route.wage
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

    private lateinit var logger: Logger
    val log: Logger? get() = if (BuildConfig.DEBUG) logger else null

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
        embeddedServer(Netty, applicationEngineEnvironment {
            logger = log
            connector {
                host = "0.0.0.0"
                port = 8080
            }
            module {
                if (BuildConfig.DEBUG) install(CallLogging)
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
                            GsonConverter(GsonBuilder().registerJodaTime().create())
                        )
                        if (BuildConfig.DEBUG) setPrettyPrinting()
                    }
                }
                routing {
                    auth()
                    customer()
                    dateTime()
                    globalSetting()
                    invoice()
                    log()
                    platePrice()
                    offsetPrice()
                    digitalPrice()
                    employee()
                    payment()
                    recess()
                    wage()
                }
            }
            Server.log?.info("Welcome to ${BuildConfig.NAME} ${BuildConfig.VERSION}")
            Server.log?.info("For more information, visit ${BuildConfig.WEBSITE}")
            Server.log?.info("Debug mode is activated, server activities will be logged here.")
        }).start(wait = true)
    }
}
