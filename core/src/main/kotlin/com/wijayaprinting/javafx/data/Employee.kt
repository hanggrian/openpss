package com.wijayaprinting.javafx.data

import com.wijayaprinting.javafx.safeTransaction
import com.wijayaprinting.mysql.dao.Wage
import com.wijayaprinting.mysql.dao.Wages
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import kotfx.bindings.doubleBindingOf
import kotfx.collections.mutableObservableListOf
import org.apache.commons.math3.util.Precision.round
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period
import java.math.BigDecimal

/**
 * Data class representing an Employee with 'no' as its identifier to avoid duplicate in [Set] scenario.
 */
data class Employee(
        /** Id and name are final values that should be determined upon xlsx reading. */
        val id: Int,
        val name: String,

        /** Attendances and shift should be set with [EmployeeTitledPane]. */
        val attendances: ObservableList<DateTime> = mutableObservableListOf(),

        /** Wages below are retrieved from sqlite, or empty if there is none. */
        val daily: IntegerProperty = SimpleIntegerProperty(),
        val hourlyOvertime: IntegerProperty = SimpleIntegerProperty(),
        val recess: DoubleProperty = SimpleDoubleProperty()
) {

    companion object {
        const val WORKING_HOURS = 8.0
    }

    init {
        safeTransaction {
            Wage.findById(id)?.let { wage ->
                daily.value = wage.daily
                hourlyOvertime.value = wage.hourlyOvertime
                recess.value = wage.recess.toDouble()
            }
        }
    }

    fun saveWage() = safeTransaction {
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (Wage.findById(id)) {
            null -> Wage.new(id) {
                daily = this@Employee.daily.value
                hourlyOvertime = this@Employee.hourlyOvertime.value
                recess = BigDecimal.valueOf(this@Employee.recess.value)
            }
            else -> Wages.update({ Wages.id eq id }) {
                it[daily] = this@Employee.daily.value
                it[hourlyOvertime] = this@Employee.hourlyOvertime.value
                it[recess] = BigDecimal.valueOf(this@Employee.recess.value)
            }
        }
    }

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Employee && other.id == id

    override fun toString(): String = "$id. $name"

    fun toNodeRecord(): Record = Record(Record.TYPE_NODE, this, DateTime.now(), DateTime.now())

    fun toChildRecords(): Set<Record> {
        val records = mutableSetOf<Record>()
        val iterator = attendances.iterator()
        while (iterator.hasNext()) {
            records.add(Record(Record.TYPE_CHILD, this, iterator.next(), iterator.next()))
        }
        return records
    }

    fun toTotalRecords(childs: Collection<Record>): Record = Record(Record.TYPE_TOTAL, this, DateTime(0), DateTime(0)).apply {
        childs.map { it.daily }.toTypedArray().let { mains ->
            daily.bind(doubleBindingOf(*mains) {
                round(mains.map { it.value }.sum(), 2)
            })
        }
        childs.map { it.dailyIncome }.toTypedArray().let { mainIncomes ->
            dailyIncome.bind(doubleBindingOf(*mainIncomes) {
                round(mainIncomes.map { it.value }.sum(), 2)
            })
        }
        childs.map { it.overtime }.toTypedArray().let { overtimes ->
            overtime.bind(doubleBindingOf(*overtimes) {
                round(overtimes.map { it.value }.sum(), 2)
            })
        }
        childs.map { it.overtimeIncome }.toTypedArray().let { overtimeIncomes ->
            overtimeIncome.bind(doubleBindingOf(*overtimeIncomes) {
                round(overtimeIncomes.map { it.value }.sum(), 2)
            })
        }
        childs.map { it.total }.toTypedArray().let { totals ->
            total.bind(doubleBindingOf(*totals) {
                round(totals.map { it.value }.sum(), 2)
            })
        }
    }

    fun mergeDuplicates() = attendances.removeAll((0 until (attendances.size - 1))
            .filter { Period(attendances[it], attendances[it + 1]).toStandardMinutes().isLessThan(minutes(5)) }
            .map { attendances[it] })
}