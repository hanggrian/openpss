package com.wijayaprinting.manager.data

import com.wijayaprinting.PATTERN_DATETIME
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Resourced
import com.wijayaprinting.manager.utils.round
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.beans.property.*
import kotfx.bind
import kotfx.doubleBindingOf
import kotfx.plus
import kotfx.stringBindingOf
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

data class AttendanceRecord @JvmOverloads constructor(
        val resourced: Resourced,

        val type: Int,
        val index: Int, // index of child, ignore if type is not child

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
                    val interval = Interval(start, end)
                    val recessesInterval = attendee.recesses.map { it.getInterval(start, end) }
                    val workingHours = {
                        var minutes = interval.toDuration().toStandardMinutes().minutes
                        recessesInterval.forEach { minutes -= interval.overlap(it)?.toDuration()?.toStandardMinutes()?.minutes ?: 0 }
                        minutes / 60.0
                    }
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
                            else -> overtime
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
                    TYPE_CHILD -> attendee.recesses.getOrNull(index)?.let { safeTransaction { it.toString() } } ?: ""
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
                    TYPE_CHILD -> start.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> ""
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    val displayedEnd: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(endProperty) {
                when (type) {
                    TYPE_NODE -> ""
                    TYPE_CHILD -> end.toString(PATTERN_DATETIME)
                    TYPE_TOTAL -> "TOTAL"
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    fun cloneStart(time: LocalTime): DateTime = DateTime(start.year, start.monthOfYear, start.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    fun cloneEnd(time: LocalTime): DateTime = DateTime(end.year, end.monthOfYear, end.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    private var start: DateTime
        get() = startProperty.get()
        set(value) = startProperty.setValue(value)

    private var end: DateTime
        get() = endProperty.get()
        set(value) = endProperty.setValue(value)
}