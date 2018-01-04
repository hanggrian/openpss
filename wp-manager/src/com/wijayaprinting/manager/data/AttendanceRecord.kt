package com.wijayaprinting.manager.data

import com.wijayaprinting.PATTERN_DATETIME
import com.wijayaprinting.manager.utils.round
import javafx.beans.property.*
import kotfx.bind
import kotfx.doubleBindingOf
import kotfx.plus
import kotfx.stringBindingOf
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.Period
import java.lang.Math.abs

data class AttendanceRecord @JvmOverloads constructor(
        val type: Int,
        val actualAttendee: Attendee,
        val start: ObjectProperty<DateTime>,
        val end: ObjectProperty<DateTime>,

        val daily: DoubleProperty = SimpleDoubleProperty(),
        val overtime: DoubleProperty = SimpleDoubleProperty(),

        val dailyIncome: DoubleProperty = SimpleDoubleProperty(),
        val overtimeIncome: DoubleProperty = SimpleDoubleProperty(),

        val total: DoubleProperty = SimpleDoubleProperty()
) {
    companion object {
        private const val WORKING_HOURS = 8.0

        /** Dummy since [javafx.scene.control.TreeTableView] must have a root item. */
        const val TYPE_ROOT = 0
        /** Parent row displaying attendee and its preferences. */
        const val TYPE_NODE = 1
        /** Child row of a node, displaying an actual record data. */
        const val TYPE_CHILD = 2
        /** Last child row of a node, displaying calculated total. */
        const val TYPE_TOTAL = 3
    }

    val attendee: Attendee?
        get() = when (type) {
            TYPE_NODE -> actualAttendee
            TYPE_CHILD -> null
            TYPE_TOTAL -> null
            else -> throw UnsupportedOperationException()
        }

    val startString: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(start) {
                when (type) {
                    TYPE_NODE -> actualAttendee.role ?: ""
                    TYPE_CHILD -> start.value.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> ""
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    val endString: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(end) {
                when (type) {
                    TYPE_NODE -> "${actualAttendee.recess.value}\t${actualAttendee.recessOvertime.value}"
                    TYPE_CHILD -> end.value.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> "TOTAL"
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    fun cloneStart(time: LocalTime) = DateTime(start.value.year, start.value.monthOfYear, start.value.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    fun cloneEnd(time: LocalTime) = DateTime(end.value.year, end.value.monthOfYear, end.value.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    init {
        if (type != TYPE_ROOT) {
            dailyIncome bind doubleBindingOf(daily, actualAttendee.daily) { (daily.value * actualAttendee.daily.value / WORKING_HOURS).round }
            overtimeIncome bind doubleBindingOf(overtime, actualAttendee.hourlyOvertime) { (actualAttendee.hourlyOvertime.value * overtime.value).round }
            when (type) {
                TYPE_NODE -> {
                    daily.set(0.0)
                    overtime.set(0.0)
                    total.set(0.0)
                }
                TYPE_CHILD -> {
                    val workingHours = { (abs(Period(start.value, end.value).toStandardMinutes().minutes) / 60.0) - actualAttendee.recess.value }
                    daily bind doubleBindingOf(start, end) {
                        val hours = workingHours()
                        when {
                            hours <= WORKING_HOURS -> hours.round
                            else -> WORKING_HOURS
                        }
                    }
                    overtime bind doubleBindingOf(start, end) {
                        val hours = workingHours()
                        val overtime = (hours - WORKING_HOURS).round
                        when {
                            hours <= WORKING_HOURS -> 0.0
                            overtime <= actualAttendee.recessOvertime.value -> overtime
                            else -> (overtime - actualAttendee.recessOvertime.value).round
                        }
                    }
                    total bind dailyIncome + overtimeIncome
                }
                TYPE_TOTAL -> total bind dailyIncome + overtimeIncome
            }
        }
    }
}