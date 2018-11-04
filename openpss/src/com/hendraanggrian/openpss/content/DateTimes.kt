@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.content

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

const val PATTERN_DATE = "MM/dd/yyyy" // to comply with JavaFX's DatePicker
const val PATTERN_TIME = "HH:mm"
const val PATTERN_DATETIME = "$PATTERN_DATE $PATTERN_TIME"
const val PATTERN_DATETIME_MULTILINE = "$PATTERN_DATE\n$PATTERN_TIME"
const val PATTERN_DATETIME_EXTENDED = "$PATTERN_DATE EEE $PATTERN_TIME"

/** For dummy purposes. */
val START_OF_TIME = DateTime(0)

/** Converts Joda's date to Java's. */
inline fun LocalDate.toJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)

/** Converts Java's date to Joda's. */
inline fun java.time.LocalDate.toJoda(): LocalDate = LocalDate(year, monthValue, dayOfMonth)

/** Converts Joda's time to Java's. */
inline fun LocalTime.toJava(): java.time.LocalTime = java.time.LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute)

/** Converts Java's time to Joda's. */
inline fun java.time.LocalTime.toJoda(): LocalTime = LocalTime(hour, minute, second)