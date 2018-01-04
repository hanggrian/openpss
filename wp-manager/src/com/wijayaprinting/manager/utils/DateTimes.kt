@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.utils

inline fun java.time.LocalDate.asJoda(): org.joda.time.LocalDate = org.joda.time.LocalDate(year, monthValue, dayOfMonth)

inline fun org.joda.time.LocalDate.asJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)