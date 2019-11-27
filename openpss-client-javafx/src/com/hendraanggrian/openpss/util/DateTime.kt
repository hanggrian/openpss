package com.hendraanggrian.openpss.util

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/** For dummy purposes. */
val START_OF_TIME = DateTime(0)

fun DateTime.trimMinutes(): DateTime = minusMinutes(minuteOfHour)
fun LocalTime.trimMinutes(): LocalTime = minusMinutes(minuteOfHour)

fun LocalDate.toJava(): java.time.LocalDate = java.time.LocalDate.of(year, monthOfYear, dayOfMonth)
fun LocalTime.toJava(): java.time.LocalTime = java.time.LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute)

fun java.time.LocalDate.toJoda(): LocalDate = LocalDate(year, monthValue, dayOfMonth)
fun java.time.LocalTime.toJoda(): LocalTime = LocalTime(hour, minute, second)
