@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.server.util

import io.ktor.application.ApplicationCall
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

inline fun ApplicationCall.getString(name: String): String = parameters[name]!!

inline fun ApplicationCall.getStringOrNull(name: String): String? = parameters[name]

inline fun ApplicationCall.getInt(name: String): Int = getString(name).toInt()

inline fun ApplicationCall.getIntOrNull(name: String): Int? = getStringOrNull(name)?.toInt()

inline fun ApplicationCall.getDouble(name: String): Double = getString(name).toDouble()

inline fun ApplicationCall.getDoubleOrNull(name: String): Double? = getStringOrNull(name)?.toDouble()

inline fun ApplicationCall.getBoolean(name: String): Boolean = getString(name).toBoolean()

inline fun ApplicationCall.getBooleanOrNull(name: String): Boolean? = getStringOrNull(name)?.toBoolean()

inline fun ApplicationCall.getLocalTime(name: String): LocalTime = LocalTime.parse(getString(name))

inline fun ApplicationCall.getLocalTimeOrNull(name: String): LocalTime? =
    getStringOrNull(name)?.let { LocalTime.parse(it) }

inline fun ApplicationCall.getLocalDate(name: String): LocalDate = LocalDate.parse(getString(name))

inline fun ApplicationCall.getLocalDateOrNull(name: String): LocalDate? =
    getStringOrNull(name)?.let { LocalDate.parse(it) }

inline fun ApplicationCall.getLocalDateTime(name: String): LocalDateTime = LocalDateTime.parse(getString(name))

inline fun ApplicationCall.getLocalDateTimeOrNull(name: String): LocalDateTime? =
    getStringOrNull(name)?.let { LocalDateTime.parse(it) }

inline fun ApplicationCall.getDateTime(name: String): DateTime = DateTime.parse(getString(name))

inline fun ApplicationCall.getDateTimeOrNull(name: String): DateTime? =
    getStringOrNull(name)?.let { DateTime.parse(it) }