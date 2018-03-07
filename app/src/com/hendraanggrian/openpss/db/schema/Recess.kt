package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.time.PATTERN_TIME
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.time
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

object Recesses : DocumentSchema<Recess>("recess", Recess::class) {
    val start = time("start")
    val end = time("end")
}

data class Recess(
    var start: LocalTime,
    var end: LocalTime
) : Document<Recesses> {
    override lateinit var id: Id<String, Recesses>

    override fun toString(): String = "${start.toString(PATTERN_TIME)} - ${end.toString(PATTERN_TIME)}"

    fun getInterval(dateTime: DateTime): Interval = Interval(start.toDateTime(dateTime), end.toDateTime(dateTime))
}