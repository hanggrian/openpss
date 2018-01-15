@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.util

/** Converts Java's date to Joda's. */
inline fun java.time.LocalDate.asJoda(): org.joda.time.LocalDate = org.joda.time.LocalDate(year, monthValue, dayOfMonth)

/** Converts Joda's date to Java's. */
inline fun org.joda.time.LocalDate.asJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)