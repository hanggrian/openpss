package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import java.util.ResourceBundle

/** Define API routing Spek-style. */
open class Routing(val block: RouteWrapper.() -> Unit) {

    val resources: ResourceBundle
        get() = Language.ofFullCode(transaction {
            findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value
        }).toResourcesBundle()
}

class RouteWrapper(private val route: Route) {

    operator fun String.invoke(block: Route.() -> Unit): Route = route.route(this, block)
}

fun Application.installRoutings(vararg routings: Routing) = routing {
    routings.forEach {
        it.block(RouteWrapper(this))
    }
}

fun ApplicationCall.getString(name: String): String = parameters[name]!!

fun ApplicationCall.getStringOrNull(name: String): String? = parameters[name]

fun ApplicationCall.getInt(name: String): Int = getString(name).toInt()

fun ApplicationCall.getIntOrNull(name: String): Int? = getStringOrNull(name)?.toInt()

fun ApplicationCall.getDouble(name: String): Double = getString(name).toDouble()

fun ApplicationCall.getDoubleOrNull(name: String): Double? = getStringOrNull(name)?.toDouble()

fun ApplicationCall.getBoolean(name: String): Boolean = getString(name).toBoolean()

fun ApplicationCall.getBooleanOrNull(name: String): Boolean? = getStringOrNull(name)?.toBoolean()

fun ApplicationCall.getLocalTime(name: String): LocalTime = LocalTime.parse(getString(name))

fun ApplicationCall.getLocalTimeOrNull(name: String): LocalTime? =
    getStringOrNull(name)?.let { LocalTime.parse(it) }

fun ApplicationCall.getLocalDate(name: String): LocalDate = LocalDate.parse(getString(name))

fun ApplicationCall.getLocalDateOrNull(name: String): LocalDate? =
    getStringOrNull(name)?.let { LocalDate.parse(it) }

fun ApplicationCall.getLocalDateTime(name: String): LocalDateTime = LocalDateTime.parse(getString(name))

fun ApplicationCall.getLocalDateTimeOrNull(name: String): LocalDateTime? =
    getStringOrNull(name)?.let { LocalDateTime.parse(it) }

fun ApplicationCall.getDateTime(name: String): DateTime = DateTime.parse(getString(name))

fun ApplicationCall.getDateTimeOrNull(name: String): DateTime? =
    getStringOrNull(name)?.let { DateTime.parse(it) }