package com.wijayaprinting.dao

import com.wijayaprinting.scene.PATTERN_TIME
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

object Recesses : IntIdTable("recess") {
    val start = datetime("start")
    val end = datetime("end")
}

class Recess(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Recess>(Recesses) {
        private val START_OF_TIME = DateTime(0)
    }

    private var mStart by Recesses.start
    private var mEnd by Recesses.end

    var start: LocalTime
        get() = mStart.toLocalTime()
        set(value) {
            mStart = value.toDateTime(START_OF_TIME)
        }

    var end: LocalTime
        get() = mEnd.toLocalTime()
        set(value) {
            mEnd = value.toDateTime(START_OF_TIME)
        }

    override fun toString(): String = "${start.toString(PATTERN_TIME)} - ${end.toString(PATTERN_TIME)}"

    fun getInterval(dateTime: DateTime): Interval = Interval(start.toDateTime(dateTime), end.toDateTime(dateTime))
}