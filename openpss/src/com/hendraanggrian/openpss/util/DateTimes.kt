@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import org.joda.time.LocalDate
import org.joda.time.LocalTime

/** Converts Joda's date to Java's. */
inline fun LocalDate.toJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)

/** Converts Java's date to Joda's. */
inline fun java.time.LocalDate.toJoda(): LocalDate = LocalDate(year, monthValue, dayOfMonth)

/** Converts Joda's time to Java's. */
inline fun LocalTime.toJava(): java.time.LocalTime = java.time.LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute)

/** Converts Java's time to Joda's. */
inline fun java.time.LocalTime.toJoda(): LocalTime = LocalTime(hour, minute, second)