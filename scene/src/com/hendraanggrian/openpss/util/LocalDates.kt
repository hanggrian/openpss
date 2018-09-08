@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import org.joda.time.LocalDate
import java.time.LocalDate.of

/** Converts Joda's date to Java's. */
inline fun LocalDate.toJava(): java.time.LocalDate = of(year, monthOfYear, dayOfMonth)

/** Converts Java's date to Joda's. */
inline fun java.time.LocalDate.toJoda(): LocalDate = LocalDate(year, monthValue, dayOfMonth)