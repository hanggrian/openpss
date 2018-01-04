package com.wijayaprinting.manager.data

import com.wijayaprinting.PATTERN_DATETIME
import com.wijayaprinting.manager.utils.abs
import com.wijayaprinting.manager.utils.round
import javafx.beans.property.*
import kotfx.bind
import kotfx.doubleBindingOf
import kotfx.plus
import kotfx.stringBindingOf
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.Period

data class AttendanceRecord @JvmOverloads constructor(
        val type: Int,
        val attendee: Attendee,
        val start: ObjectProperty<DateTime>,
        val end: ObjectProperty<DateTime>,

        val dailyEmpty: BooleanProperty = SimpleBooleanProperty(),

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
        /** Parent row displaying displayedAttendee and its preferences. */
        const val TYPE_NODE = 1
        /** Child row of a node, displaying an actual record data. */
        const val TYPE_CHILD = 2
        /** Last child row of a node, displaying calculated total. */
        const val TYPE_TOTAL = 3
    }

    init {
        if (type != TYPE_ROOT) {
            dailyEmpty.set(false)
            dailyIncome bind doubleBindingOf(daily, attendee.daily) { (daily.value * attendee.daily.value / WORKING_HOURS).round }
            overtimeIncome bind doubleBindingOf(overtime, attendee.hourlyOvertime) { (attendee.hourlyOvertime.value * overtime.value).round }
            when (type) {
                TYPE_NODE -> {
                    daily.set(0.0)
                    overtime.set(0.0)
                    total.set(0.0)
                }
                TYPE_CHILD -> {
                    val workingHours = { (Period(start.value, end.value).toStandardMinutes().minutes.abs / 60.0) - attendee.recess.value }
                    daily bind doubleBindingOf(start, end, dailyEmpty) {
                        if (dailyEmpty.value) 0.0 else {
                            val hours = workingHours()
                            when {
                                hours <= WORKING_HOURS -> hours.round
                                else -> WORKING_HOURS
                            }
                        }
                    }
                    overtime bind doubleBindingOf(start, end) {
                        val hours = workingHours()
                        val overtime = (hours - WORKING_HOURS).round
                        when {
                            hours <= WORKING_HOURS -> 0.0
                            overtime <= attendee.recessOvertime.value -> overtime
                            else -> (overtime - attendee.recessOvertime.value).round
                        }
                    }
                    total bind dailyIncome + overtimeIncome
                }
                TYPE_TOTAL -> total bind dailyIncome + overtimeIncome
            }
        }
    }

    val displayedAttendee: Attendee?
        get() = when (type) {
            TYPE_NODE -> attendee
            TYPE_CHILD -> null
            TYPE_TOTAL -> null
            else -> throw UnsupportedOperationException()
        }

    val displayedStart: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(start) {
                when (type) {
                    TYPE_NODE -> attendee.role ?: ""
                    TYPE_CHILD -> start.value.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> ""
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    val displayedEnd: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(end) {
                when (type) {
                    TYPE_NODE -> "${attendee.recess.value}\t${attendee.recessOvertime.value}"
                    TYPE_CHILD -> end.value.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> "TOTAL"
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    fun cloneStart(time: LocalTime): DateTime = DateTime(start.value.year, start.value.monthOfYear, start.value.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    fun cloneEnd(time: LocalTime): DateTime = DateTime(end.value.year, end.value.monthOfYear, end.value.dayOfMonth, time.hourOfDay, time.minuteOfHour)
}