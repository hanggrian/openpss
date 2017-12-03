package com.wijayaprinting.manager.data

import com.hendraanggrian.rxexposed.SQLCompletables
import com.hendraanggrian.rxexposed.SQLSingles
import com.wijayaprinting.data.Wage
import com.wijayaprinting.data.Wages
import com.wijayaprinting.manager.utils.multithread
import io.reactivex.rxkotlin.subscribeBy
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import kotfx.collections.mutableObservableListOf
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period
import java.math.BigDecimal
import java.util.Optional.ofNullable

/** Data class representing an Employee with 'no' as its identifier to avoid duplicates in [Set] scenario. */
data class Employee @JvmOverloads constructor(
        /** Id and name are final values that should be determined upon xlsx reading. */
        val id: Int,
        val name: String,
        val role: String? = null,

        /** Attendances and shift should be set with [EmployeeTitledPane]. */
        val attendances: ObservableList<DateTime> = mutableObservableListOf(),
        private val duplicates: ObservableList<DateTime> = mutableObservableListOf(),

        /** Wages below are retrieved from sql, or empty if there is none. */
        val daily: IntegerProperty = SimpleIntegerProperty(),
        val hourlyOvertime: IntegerProperty = SimpleIntegerProperty(),
        val recess: DoubleProperty = SimpleDoubleProperty(),
        val recessOvertime: DoubleProperty = SimpleDoubleProperty()
) {
    companion object {
        const val WORKING_HOURS = 8.0
    }

    init {
        SQLSingles.transaction { ofNullable(Wage.findById(id)) }
                .multithread()
                .filter { it.isPresent }
                .map { it.get() }
                .subscribeBy({}) { wage ->
                    daily.value = wage.daily
                    hourlyOvertime.value = wage.hourlyOvertime
                    recess.value = wage.recess.toDouble()
                    recessOvertime.value = wage.recessOvertime.toDouble()
                }
    }

    fun saveWage() = SQLCompletables
            .transaction {
                @Suppress("IMPLICIT_CAST_TO_ANY")
                if (Wage.findById(id) == null) Wage.new(id) {
                    daily = this@Employee.daily.value
                    hourlyOvertime = this@Employee.hourlyOvertime.value
                    recess = BigDecimal.valueOf(this@Employee.recess.value)
                    recessOvertime = BigDecimal.valueOf(this@Employee.recessOvertime.value)
                } else Wages.update({ Wages.id eq id }) { wage ->
                    wage[daily] = this@Employee.daily.value
                    wage[hourlyOvertime] = this@Employee.hourlyOvertime.value
                    wage[recess] = BigDecimal.valueOf(this@Employee.recess.value)
                    wage[recessOvertime] = BigDecimal.valueOf(this@Employee.recessOvertime.value)
                }
            }
            .multithread()
            .subscribeBy({}) {}

    fun addAttendance(element: DateTime) {
        attendances.add(element)
        duplicates.add(element)
    }

    fun addAllAttendances(elements: Collection<DateTime>) {
        attendances.addAll(elements)
        duplicates.addAll(elements)
    }

    fun revert() {
        attendances.clear()
        attendances.addAll(duplicates)
    }

    fun mergeDuplicates() = (0 until (attendances.size - 1))
            .filter { Period(attendances[it], attendances[it + 1]).toStandardMinutes().isLessThan(minutes(5)) }
            .map { attendances[it] }
            .let { toRemove ->
                attendances.removeAll(toRemove)
                duplicates.removeAll(toRemove)
            }

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Employee && other.id == id

    override fun toString(): String = "$id. $name"
}