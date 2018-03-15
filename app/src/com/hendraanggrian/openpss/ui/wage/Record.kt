package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.time.FlexibleInterval
import com.hendraanggrian.openpss.time.PATTERN_DATETIME
import com.hendraanggrian.openpss.time.START_OF_TIME
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.util.round
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import kfx.beans.binding.doubleBindingOf
import kfx.beans.binding.plus
import kfx.beans.binding.stringBindingOf
import kfx.beans.property.toProperty
import org.joda.time.DateTime
import org.joda.time.LocalTime
import kotlin.math.absoluteValue

class Record(
    resourced: Resourced,

    val index: Int,
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
        const val WORKING_HOURS = 8

        /** Parent row displaying name and its preferences. */
        const val INDEX_NODE = -2
        /** Last child row of a node, displaying calculated total. */
        const val INDEX_TOTAL = -1

        /** Dummy for invisible [javafx.scene.control.TreeTableView] root. */
        fun getDummy(resourced: Resourced) = Record(resourced, Int.MIN_VALUE, Attendee.DUMMY,
            START_OF_TIME.toProperty(), START_OF_TIME.toProperty())
    }

    init {
        dailyEmptyProperty.set(false)
        if (isNode) {
            dailyProperty.set(0.0)
            overtimeProperty.set(0.0)
            dailyIncomeProperty.set(attendee.daily.toDouble())
            overtimeIncomeProperty.set(attendee.hourlyOvertime.toDouble())
            totalProperty.set(0.0)
        }
        if (isChild) {
            dailyProperty.bind(doubleBindingOf(startProperty, endProperty, dailyEmptyProperty) {
                if (isDailyEmpty) 0.0 else {
                    val hours = workingHours
                    when {
                        hours <= WORKING_HOURS -> hours.round()
                        else -> WORKING_HOURS.toDouble()
                    }
                }
            })
            overtimeProperty.bind(doubleBindingOf(startProperty, endProperty) {
                val hours = workingHours
                val overtime = (hours - WORKING_HOURS).round()
                when {
                    hours <= WORKING_HOURS -> 0.0
                    else -> overtime
                }
            })
        }
        if (isChild || isTotal) {
            dailyIncomeProperty.bind(doubleBindingOf(dailyProperty) {
                (daily * attendee.daily / WORKING_HOURS).round()
            })
            overtimeIncomeProperty.bind(doubleBindingOf(overtimeProperty) {
                (overtime * attendee.hourlyOvertime).round()
            })
            totalProperty.bind(dailyIncomeProperty + overtimeIncomeProperty)
        }
    }

    val isNode: Boolean get() = index == INDEX_NODE
    val isTotal: Boolean get() = index == INDEX_TOTAL
    val isChild: Boolean get() = index >= 0

    val displayedName: String
        get() = when {
            isNode -> attendee.toString()
            isChild -> attendee.recesses.getOrNull(index)?.toString() ?: ""
            isTotal -> ""
            else -> throw UnsupportedOperationException()
        }

    val displayedStart: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(startProperty, dailyEmptyProperty) {
                when {
                    isNode -> attendee.role ?: ""
                    isChild -> start.toString(PATTERN_DATETIME).let { if (isDailyEmpty) "($it)" else it }
                    isTotal -> ""
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    val displayedEnd: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(endProperty, dailyEmptyProperty) {
                when {
                    isNode -> "${attendee.attendances.size / 2} ${getString(R.string.day)}"
                    isChild -> end.toString(PATTERN_DATETIME).let { if (isDailyEmpty) "($it)" else it }
                    isTotal -> "TOTAL"
                    else -> throw UnsupportedOperationException()
                }
            })
        }

    fun cloneStart(time: LocalTime): DateTime =
        DateTime(start.year, start.monthOfYear, start.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    fun cloneEnd(time: LocalTime): DateTime =
        DateTime(end.year, end.monthOfYear, end.dayOfMonth, time.hourOfDay, time.minuteOfHour)

    private var start: DateTime
        get() = startProperty.get()
        set(value) = startProperty.setValue(value)

    private var end: DateTime
        get() = endProperty.get()
        set(value) = endProperty.setValue(value)

    private var isDailyEmpty: Boolean
        get() = dailyEmptyProperty.get()
        set(value) = dailyEmptyProperty.set(value)

    private var daily: Double
        get() = dailyProperty.get()
        set(value) = dailyProperty.set(value)

    private var overtime: Double
        get() = overtimeProperty.get()
        set(value) = overtimeProperty.set(value)

    private val workingHours: Double
        get() {
            val interval = FlexibleInterval(start, end)
            var minutes = interval.minutes
            attendee.recesses
                .map { it.getInterval(start) }
                .forEach {
                    minutes -= interval.overlap(it)?.toDuration()?.toStandardMinutes()?.minutes?.absoluteValue ?: 0
                }
            return minutes / 60.0
        }
}