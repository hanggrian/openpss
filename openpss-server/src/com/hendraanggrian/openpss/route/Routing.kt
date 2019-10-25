package com.hendraanggrian.openpss.route

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

suspend inline fun ApplicationCall.respondError(
    desc: String,
    statusProvider: HttpStatusCode.Companion.() -> HttpStatusCode = { NotAcceptable }
) = respond(statusProvider(HttpStatusCode).description(desc))

suspend inline fun ApplicationCall.respondSuccess(
    message: Any,
    statusProvider: HttpStatusCode.Companion.() -> HttpStatusCode = { OK }
) = respond(statusProvider(HttpStatusCode), message)

fun <T> Iterable<T>.isEmpty(): Boolean = count() == 0

fun <T> Iterable<T>.isNotEmpty(): Boolean = count() != 0

fun ApplicationCall.getString(name: String): String = parameters[name]!!

fun ApplicationCall.getStringOrNull(name: String): String? = parameters[name]

fun ApplicationCall.getInt(name: String): Int = getString(name).toInt()

fun ApplicationCall.getIntOrNull(name: String): Int? = getStringOrNull(name)?.toInt()

fun ApplicationCall.getDouble(name: String): Double = getString(name).toDouble()

fun ApplicationCall.getDoubleOrNull(name: String): Double? =
    getStringOrNull(name)?.toDouble()

fun ApplicationCall.getBoolean(name: String): Boolean = getString(name).toBoolean()

fun ApplicationCall.getBooleanOrNull(name: String): Boolean? =
    getStringOrNull(name)?.toBoolean()

fun ApplicationCall.getLocalTime(name: String): LocalTime = LocalTime.parse(getString(name))

fun ApplicationCall.getLocalTimeOrNull(name: String): LocalTime? =
    getStringOrNull(name)?.let { LocalTime.parse(it) }

fun ApplicationCall.getLocalDate(name: String): LocalDate = LocalDate.parse(getString(name))

fun ApplicationCall.getLocalDateOrNull(name: String): LocalDate? =
    getStringOrNull(name)?.let { LocalDate.parse(it) }

fun ApplicationCall.getLocalDateTime(name: String): LocalDateTime =
    LocalDateTime.parse(getString(name))

fun ApplicationCall.getLocalDateTimeOrNull(name: String): LocalDateTime? =
    getStringOrNull(name)?.let { LocalDateTime.parse(it) }

fun ApplicationCall.getDateTime(name: String): DateTime = DateTime.parse(getString(name))

fun ApplicationCall.getDateTimeOrNull(name: String): DateTime? =
    getStringOrNull(name)?.let { DateTime.parse(it) }
