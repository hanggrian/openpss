package com.wijayaprinting.manager.data

import com.wijayaprinting.PATTERN_DATETIME
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Resourced
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
        val resourced: Resourced,

        val type: Int,
        val attendee: Attendee,
        val startProperty: ObjectProperty<DateTime>,
        val endProperty: ObjectProperty<DateTime>,

        val dailyEmptyProperty: BooleanProperty = SimpleBooleanProperty(),

        val dailyProperty: DoubleProperty = SimpleDoubleProperty(),
        val overtimeProperty: DoubleProperty = SimpleDoubleProperty(),

        val dailyIncomeProperty: DoubleProperty = SimpleDoubleProperty(),
        val overtimeIncomeProperty: DoubleProperty = SimpleDoubleProperty(),

        val totalProperty: DoubleProperty = SimpleDoubleProperty()
) : Resourced by resourced {

    companion object {
        private const val WORKING_HOURS = 8.0

        /** Dummy since [javafx.scene.control.TreeTableView] must have a root item. */
        const val TYPE_ROOT = 0
        /** Parent row displaying name and its preferences. */
        const val TYPE_NODE = 1
        /** Child row of a node, displaying an actual record data. */
        const val TYPE_CHILD = 2
        /** Last child row of a node, displaying calculated total. */
        const val TYPE_TOTAL = 3
    }

    init {
        if (type != TYPE_ROOT) {
            dailyEmptyProperty.set(false)
            dailyIncomeProperty bind doubleBindingOf(dailyProperty, attendee.dailyProperty) { (dailyProperty.value * attendee.dailyProperty.value / WORKING_HOURS).round }
            overtimeIncomeProperty bind doubleBindingOf(overtimeProperty, attendee.hourlyOvertimeProperty) { (attendee.hourlyOvertimeProperty.value * overtimeProperty.value).round }
            when (type) {
                TYPE_NODE -> {
                    dailyProperty.set(0.0)
                    overtimeProperty.set(0.0)
                    totalProperty.set(0.0)
                }
                TYPE_CHILD -> {
                    val workingHours = { (Period(startProperty.value, endProperty.value).toStandardMinutes().minutes.abs / 60.0) - attendee.recessProperty.value }
                    dailyProperty bind doubleBindingOf(startProperty, endProperty, dailyEmptyProperty) {
                        if (dailyEmptyProperty.value) 0.0 else {
                            val hours = workingHours()
                            when {
                                hours <= WORKING_HOURS -> hours.round
                                else -> WORKING_HOURS
                            }
                        }
                    }
                    overtimeProperty bind doubleBindingOf(startProperty, endProperty) {
                        val hours = workingHours()
                        val overtime = (hours - WORKING_HOURS).round
                        when {
                            hours <= WORKING_HOURS -> 0.0
                            overtime <= attendee.recessOvertimeProperty.value -> overtime
                            else -> (overtime - attendee.recessOvertimeProperty.value).round
                        }
                    }
                    totalProperty bind dailyIncomeProperty + overtimeIncomeProperty
                }
                TYPE_TOTAL -> totalProperty bind dailyIncomeProperty + overtimeIncomeProperty
            }
        }
    }

    val displayedName: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(dailyEmptyProperty) {
                if (dailyEmptyProperty.value) getString(R.string.daily_emptied) else when (type) {
                    TYPE_NODE -> attendee.toString()
                    TYPE_CHILD -> ""
                    TYPE_TOTAL -> ""
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    val displayedStart: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(startProperty) {
                when (type) {
                    TYPE_NODE -> attendee.role ?: ""
                    TYPE_CHILD -> startProperty.value.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> ""
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    val displayedEnd: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(endProperty) {
                when (type) {
                    TYPE_NODE -> "${attendee.recessProperty.value}\t${attendee.recessOvertimeProperty.value}"
                    TYPE_CHILD -> endProperty.value.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> "TOTAL"
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    fun cloneStart(time: LocalTime): DateTime = DateTime(startProperty.value.year, startProperty.value.monthOfYear, startProperty.value.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    fun cloneEnd(time: LocalTime): DateTime = DateTime(endProperty.value.year, endProperty.value.monthOfYear, endProperty.value.dayOfMonth, time.hourOfDay, time.minuteOfHour)
}