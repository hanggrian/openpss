package com.hanggrian.openpss.db.schemas

import com.hanggrian.openpss.PATTERN_TIME
import com.hanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.time
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

object Recesses : DocumentSchema<Recess>("recesses", Recess::class) {
    val start = time("start")
    val end = time("end")
}

data class Recess(val start: LocalTime, val end: LocalTime) : Document<Recesses> {
    override lateinit var id: Id<String, Recesses>

    /** Get interval from [start] to [end], using [dateTime] as a basis of date. */
    fun getInterval(dateTime: DateTime): Interval =
        Interval(start.toDateTime(dateTime), end.toDateTime(dateTime))

    override fun toString(): String =
        "${start.toString(PATTERN_TIME)} - ${end.toString(PATTERN_TIME)}"
}
