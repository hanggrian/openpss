@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.time

import org.joda.time.DateTime
import org.joda.time.LocalDate

const val PATTERN_DATE = "MM/dd/yyyy" // to comply with JavaFX's DatePicker
const val PATTERN_TIME = "HH:mm"
const val PATTERN_DATETIME = "$PATTERN_DATE $PATTERN_TIME"
const val PATTERN_DATETIME_EXTENDED = "$PATTERN_DATE EEE $PATTERN_TIME"

val START_OF_TIME = DateTime(0)

/** Converts Joda's date to Java's. */
inline fun LocalDate.toJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)

/** Converts Java's date to Joda's. */
inline fun java.time.LocalDate.toJoda(): LocalDate = LocalDate(year, monthValue, dayOfMonth)