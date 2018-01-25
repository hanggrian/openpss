@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting

const val PATTERN_DATE = "MM/dd/yyyy" // to comply with JavaFX's DatePicker
const val PATTERN_TIME = "HH:mm"
const val PATTERN_DATETIME = "$PATTERN_DATE $PATTERN_TIME"

/** Converts Java's date to Joda's. */
inline fun java.time.LocalDate.asJoda(): org.joda.time.LocalDate = org.joda.time.LocalDate(year, monthValue, dayOfMonth)

/** Converts Joda's date to Java's. */
inline fun org.joda.time.LocalDate.asJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)