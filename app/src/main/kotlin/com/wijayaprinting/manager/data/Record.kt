package com.wijayaprinting.manager.data

import com.wijayaprinting.data.PATTERN_DATETIME
import com.wijayaprinting.manager.utils.round
import javafx.beans.property.*
import kotfx.bindings.doubleBindingOf
import kotfx.bindings.plus
import kotfx.bindings.stringBindingOf
import kotfx.properties.bind
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.Period
import java.lang.Math.abs

data class Record @JvmOverloads constructor(
        val type: Int,
        val actualEmployee: Employee,
        val start: SimpleObjectProperty<DateTime>,
        val end: SimpleObjectProperty<DateTime>,

        val daily: DoubleProperty = SimpleDoubleProperty(),
        val overtime: DoubleProperty = SimpleDoubleProperty(),

        val dailyIncome: DoubleProperty = SimpleDoubleProperty(),
        val overtimeIncome: DoubleProperty = SimpleDoubleProperty(),

        val total: DoubleProperty = SimpleDoubleProperty()
) {
    companion object {
        /** Dummy since [javafx.scene.control.TreeTableView] must have a root item. */
        const val TYPE_ROOT = 0
        /** Parent row displaying employee and its preferences. */
        const val TYPE_NODE = 1
        /** Child row of a node, displaying an actual record data. */
        const val TYPE_CHILD = 2
        /** Last child row of a node, displaying calculated total. */
        const val TYPE_TOTAL = 3

        val ROOT: Record = Record(TYPE_ROOT, Employee(0, ""), SimpleObjectProperty(DateTime(0)), SimpleObjectProperty(DateTime(0)))
    }

    val employee: Employee?
        get() = when (type) {
            TYPE_NODE -> actualEmployee
            TYPE_CHILD -> null
            TYPE_TOTAL -> null
            else -> throw UnsupportedOperationException()
        }

    val startString: StringProperty
        get() = SimpleStringProperty().apply {
            bind(stringBindingOf(start) {
                when (type) {
                    TYPE_NODE -> actualEmployee.role ?: ""
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
                    TYPE_NODE -> ""
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
            dailyIncome bind doubleBindingOf(daily, actualEmployee.daily) { (daily.value * actualEmployee.daily.value / Employee.WORKING_HOURS).round }
            overtimeIncome bind doubleBindingOf(overtime, actualEmployee.hourlyOvertime) { (actualEmployee.hourlyOvertime.value * overtime.value).round }
            when (type) {
                TYPE_NODE -> {
                    daily.set(actualEmployee.recess.value)
                    overtime.set(actualEmployee.recessOvertime.value)
                    total.set(0.0)
                }
                TYPE_CHILD -> {
                    val workingHours = { (abs(Period(start.value, end.value).toStandardMinutes().minutes) / 60.0) - actualEmployee.recess.value }
                    daily bind doubleBindingOf(start, end) {
                        val hours = workingHours()
                        when {
                            hours <= Employee.WORKING_HOURS -> hours.round
                            else -> Employee.WORKING_HOURS
                        }
                    }
                    overtime bind doubleBindingOf(start, end) {
                        val hours = workingHours()
                        val overtime = (hours - Employee.WORKING_HOURS).round
                        when {
                            hours <= Employee.WORKING_HOURS -> 0.0
                            overtime <= actualEmployee.recessOvertime.value -> overtime
                            else -> (overtime - actualEmployee.recessOvertime.value).round
                        }
                    }
                    total bind dailyIncome + overtimeIncome
                }
                TYPE_TOTAL -> total bind dailyIncome + overtimeIncome
            }
        }
    }
}

fun Employee.toNodeRecord(): Record = Record(Record.TYPE_NODE, this, SimpleObjectProperty(DateTime.now()), SimpleObjectProperty(DateTime.now()))

fun Employee.toChildRecords(): Set<Record> {
    val records = mutableSetOf<Record>()
    val iterator = attendances.iterator()
    while (iterator.hasNext()) {
        records.add(Record(Record.TYPE_CHILD, this, SimpleObjectProperty(iterator.next()), SimpleObjectProperty(iterator.next())))
    }
    return records
}

fun Employee.toTotalRecords(childs: Collection<Record>): Record = Record(Record.TYPE_TOTAL, this, SimpleObjectProperty(DateTime(0)), SimpleObjectProperty(DateTime(0))).apply {
    childs.map { it.daily }.toTypedArray().let { mains ->
        daily bind doubleBindingOf(*mains) {
            mains.map { it.value }.sum().round
        }
    }
    childs.map { it.dailyIncome }.toTypedArray().let { mainIncomes ->
        dailyIncome bind doubleBindingOf(*mainIncomes) {
            mainIncomes.map { it.value }.sum().round
        }
    }
    childs.map { it.overtime }.toTypedArray().let { overtimes ->
        overtime bind doubleBindingOf(*overtimes) {
            overtimes.map { it.value }.sum().round
        }
    }
    childs.map { it.overtimeIncome }.toTypedArray().let { overtimeIncomes ->
        overtimeIncome bind doubleBindingOf(*overtimeIncomes) {
            overtimeIncomes.map { it.value }.sum().round
        }
    }
    childs.map { it.total }.toTypedArray().let { totals ->
        total bind doubleBindingOf(*totals) {
            totals.map { it.value }.sum().round
        }
    }
}