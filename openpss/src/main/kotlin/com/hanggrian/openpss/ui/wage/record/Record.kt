@file:Suppress("ktlint:rulebook:if-else-flattening")

package com.hanggrian.openpss.ui.wage.record

import com.hanggrian.openpss.PATTERN_DATETIME
import com.hanggrian.openpss.R
import com.hanggrian.openpss.Resources
import com.hanggrian.openpss.ui.wage.Attendee
import com.hanggrian.openpss.ui.wage.SafeInterval
import com.hanggrian.openpss.util.START_OF_TIME
import com.hanggrian.openpss.util.round
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import ktfx.bindings.doubleBindingBy
import ktfx.bindings.doubleBindingOf
import ktfx.bindings.plus
import ktfx.bindings.stringBindingOf
import ktfx.getValue
import ktfx.propertyOf
import ktfx.setValue
import org.joda.time.DateTime
import org.joda.time.LocalTime
import kotlin.math.absoluteValue

class Record(
    resources: Resources,
    val index: Int,
    val attendee: Attendee,
    val startProperty: ObjectProperty<DateTime>,
    val endProperty: ObjectProperty<DateTime>,
    val dailyDisabledProperty: BooleanProperty = SimpleBooleanProperty(),
    val dailyProperty: DoubleProperty = SimpleDoubleProperty(),
    val overtimeProperty: DoubleProperty = SimpleDoubleProperty(),
    val dailyIncomeProperty: DoubleProperty = SimpleDoubleProperty(),
    val overtimeIncomeProperty: DoubleProperty = SimpleDoubleProperty(),
    val totalProperty: DoubleProperty = SimpleDoubleProperty(),
) : Resources by resources {
    var start: DateTime? by startProperty
    var end: DateTime? by endProperty
    var isDailyDisabled: Boolean by dailyDisabledProperty
    var daily: Double by dailyProperty
    var overtime: Double by overtimeProperty
    var dailyIncome: Double by dailyIncomeProperty
    var overtimeIncome: Double by overtimeIncomeProperty
    var total: Double by totalProperty

    init {
        dailyDisabledProperty.set(false)
        if (isNode()) {
            dailyProperty.set(0.0)
            overtimeProperty.set(0.0)
            dailyIncomeProperty.set(attendee.daily.toDouble())
            overtimeIncomeProperty.set(attendee.hourlyOvertime.toDouble())
            totalProperty.set(0.0)
        }
        if (isChild()) {
            dailyProperty.bind(
                doubleBindingOf(startProperty, endProperty, dailyDisabledProperty) {
                    if (isDailyDisabled) {
                        return@doubleBindingOf 0.0
                    }
                    val hours = workingHours
                    when {
                        hours <= WORKING_HOURS -> hours.round()
                        else -> WORKING_HOURS.toDouble()
                    }
                },
            )
            overtimeProperty.bind(
                doubleBindingOf(startProperty, endProperty) {
                    val hours = workingHours
                    val overtime = (hours - WORKING_HOURS).round()
                    when {
                        hours <= WORKING_HOURS -> 0.0
                        else -> overtime
                    }
                },
            )
        }
        if (isChild() || isTotal()) {
            dailyIncomeProperty.bind(
                dailyProperty.doubleBindingBy {
                    (it * attendee.daily / WORKING_HOURS).round()
                },
            )
            overtimeIncomeProperty.bind(
                overtimeProperty.doubleBindingBy {
                    (it * attendee.hourlyOvertime).round()
                },
            )
            totalProperty.bind(dailyIncomeProperty + overtimeIncomeProperty)
        }
    }

    fun isNode(): Boolean = index == INDEX_NODE

    fun isTotal(): Boolean = index == INDEX_TOTAL

    fun isChild(): Boolean = index >= 0

    val displayedName: String
        get() =
            when {
                isNode() -> attendee.toString()
                isChild() -> attendee.recesses.getOrNull(index)?.toString().orEmpty()
                isTotal() -> ""
                else -> throw UnsupportedOperationException()
            }

    val displayedStart: StringProperty
        get() =
            SimpleStringProperty().apply {
                bind(
                    stringBindingOf(startProperty, dailyDisabledProperty) {
                        when {
                            isNode() -> attendee.role.orEmpty()
                            isChild() ->
                                start!!
                                    .toString(PATTERN_DATETIME)
                                    .let { if (isDailyDisabled) "($it)" else it }
                            isTotal() -> ""
                            else -> throw UnsupportedOperationException()
                        }
                    },
                )
            }

    val displayedEnd: StringProperty
        get() =
            SimpleStringProperty().apply {
                bind(
                    stringBindingOf(endProperty, dailyDisabledProperty) {
                        when {
                            isNode() ->
                                "${attendee.attendances.size / 2} ${getString(R.string_day)}"
                            isChild() ->
                                end!!
                                    .toString(PATTERN_DATETIME)
                                    .let { if (isDailyDisabled) "($it)" else it }
                            isTotal() -> getString(R.string_total)
                            else -> throw UnsupportedOperationException()
                        }
                    },
                )
            }

    fun cloneStart(time: LocalTime): DateTime =
        DateTime(
            start!!.year,
            start!!.monthOfYear,
            start!!.dayOfMonth,
            time.hourOfDay,
            time.minuteOfHour,
        )

    fun cloneEnd(time: LocalTime): DateTime =
        DateTime(
            end!!.year,
            end!!.monthOfYear,
            end!!.dayOfMonth,
            time.hourOfDay,
            time.minuteOfHour,
        )

    private val workingHours: Double
        get() {
            val interval = SafeInterval.of(start!!, end!!)
            var minutes = interval.minutes
            attendee.recesses
                .map { it.getInterval(start!!) }
                .forEach {
                    minutes -=
                        interval
                            .overlap(it)
                            ?.toDuration()
                            ?.toStandardMinutes()
                            ?.minutes
                            ?.absoluteValue
                            ?: 0
                }
            return minutes / 60.0
        }

    companion object {
        const val WORKING_HOURS = 8

        /** Parent row displaying name and its settings. */
        const val INDEX_NODE = -2

        /** Last child row of a node, displaying calculated total. */
        const val INDEX_TOTAL = -1

        /** Dummy for invisible [javafx.scene.control.TreeTableView] root. */
        fun getDummy(resources: Resources) =
            Record(
                resources,
                Int.MIN_VALUE,
                Attendee.DUMMY,
                propertyOf(START_OF_TIME),
                propertyOf(START_OF_TIME),
            )
    }
}
