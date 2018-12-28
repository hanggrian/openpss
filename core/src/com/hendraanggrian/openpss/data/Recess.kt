package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.Formats
import com.hendraanggrian.openpss.schema.Recesses
import kotlinx.nosql.Id
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

data class Recess(
    val start: LocalTime,
    val end: LocalTime
) : Document<Recesses> {

    override lateinit var id: Id<String, Recesses>

    override fun toString(): String = "${start.toString(Formats.TIME)} - ${end.toString(Formats.TIME)}"

    /** Get interval from [start] to [end], using [dateTime] as a basis of date. */
    fun getInterval(dateTime: DateTime): Interval = Interval(start.toDateTime(dateTime), end.toDateTime(dateTime))
}