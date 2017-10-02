package com.wijayaprinting.javafx.data

import com.wijayaprinting.javafx.utils.safeTransaction
import com.wijayaprinting.mysql.dao.Shift
import com.wijayaprinting.mysql.dao.Wage
import com.wijayaprinting.mysql.dao.Wages
import com.wijayaprinting.mysql.utils.minutesDiff
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import kotfx.bindings.doubleBindingOf
import kotfx.collections.mutableObservableListOf
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

/**
 * Data class representing an Employee with 'no' as its identifier to avoid duplicate in [Set] scenario.
 *
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
data class Employee(
        /** Id and name are final values that should be determined upon xlsx reading. */
        val id: Int,
        val name: String,

        /** Attendances and shift should be set with [EmployeeTitledPane]. */
        val attendances: ObservableList<DateTime> = mutableObservableListOf(),
        val shift: ObservableValue<Shift> = SimpleObjectProperty<Shift>(),

        /** Wages below are retrieved from sqlite, or empty if there is none. */
        val daily: IntegerProperty = SimpleIntegerProperty(),
        val overtimeHourly: IntegerProperty = SimpleIntegerProperty()
) {

    init {
        safeTransaction {
            Wage.findById(id)?.let { wage ->
                daily.value = wage.daily
                overtimeHourly.value = wage.hourlyOvertime
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun saveWage() = safeTransaction {
        when (Wage.findById(id)) {
            null -> Wage.new(id) {
                daily = this@Employee.daily.value
                hourlyOvertime = this@Employee.overtimeHourly.value
            }
            else -> Wages.update({ Wages.id eq id }) {
                it[daily] = this@Employee.daily.value
                it[hourlyOvertime] = this@Employee.overtimeHourly.value
            }
        }
    }

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Employee && other.id == id

    override fun toString(): String = "$id. $name"

    fun toNodeRecord(): Record = Record(Record.TYPE_NODE, this, shift.value.start, shift.value.end)

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
            daily.bind(doubleBindingOf(*mains) { mains.map { it.value }.sum() })
        }
        childs.map { it.dailyIncome }.toTypedArray().let { mainIncomes ->
            dailyIncome.bind(doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum() })
        }
        childs.map { it.overtime }.toTypedArray().let { overtimes ->
            overtime.bind(doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum() })
        }
        childs.map { it.overtimeIncome }.toTypedArray().let { overtimeIncomes ->
            overtimeIncome.bind(doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum() })
        }
        childs.map { it.total }.toTypedArray().let { totals ->
            total.bind(doubleBindingOf(*totals) { totals.map { it.value }.sum() })
        }
    }

    fun mergeDuplicates() {
        val iterator = attendances.iterator()
        var temp: DateTime? = null
        while (iterator.hasNext()) {
            val record = iterator.next()
            if (temp != null && record.minutesDiff(temp) <= 5) {
                iterator.remove()
            }
            temp = record
        }
    }
}