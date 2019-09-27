package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.PATTERN_TIME
import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.Recesses
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

data class Recess(
    val start: LocalTime,
    val end: LocalTime
) : Document<Recesses> {

    override lateinit var id: StringId<Recesses>

    override fun toString(): String = start.toString(PATTERN_TIME) +
        " - " +
        end.toString(PATTERN_TIME)

    /** Get interval from [start] to [end], using [dateTime] as a basis of date. */
    fun getInterval(dateTime: DateTime): Interval =
        Interval(start.toDateTime(dateTime), end.toDateTime(dateTime))
}
