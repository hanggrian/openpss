package com.wijayaprinting.manager.data

import com.wijayaprinting.dao.Wage
import com.wijayaprinting.manager.internal.RevertableObservableList
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period
import java.math.BigDecimal

/** Data class representing an Attendee with id as its identifier to avoid duplicates in [Set] scenario. */
data class Attendee @JvmOverloads constructor(
        /** Id and name are final values that should be determined upon xlsx reading. */
        val id: Int,
        val name: String,
        val role: String? = null,

        /** Attendances and shift should be set in [com.wijayaprinting.manager.controller.AttendanceController]. */
        val attendances: RevertableObservableList<DateTime> = RevertableObservableList(),

        /** Wages below are retrieved from sql, or dailyEmpty if there is none. */
        val daily: IntegerProperty = SimpleIntegerProperty(),
        val hourlyOvertime: IntegerProperty = SimpleIntegerProperty(),
        val recess: DoubleProperty = SimpleDoubleProperty(),
        val recessOvertime: DoubleProperty = SimpleDoubleProperty()
) {

    init {
        safeTransaction { Wage.findById(id) }?.let { wage ->
            daily.value = wage.daily
            hourlyOvertime.value = wage.hourlyOvertime
            recess.value = wage.recess.toDouble()
            recessOvertime.value = wage.recessOvertime.toDouble()
        }
    }

    fun saveWage() = safeTransaction @Suppress("IMPLICIT_CAST_TO_ANY") {
        val wage = Wage.findById(id)
        if (wage == null) Wage.new(id) {
            daily = this@Attendee.daily.value
            hourlyOvertime = this@Attendee.hourlyOvertime.value
            recess = BigDecimal.valueOf(this@Attendee.recess.value)
            recessOvertime = BigDecimal.valueOf(this@Attendee.recessOvertime.value)
        } else {
            wage.daily = this@Attendee.daily.value
            wage.hourlyOvertime = this@Attendee.hourlyOvertime.value
            wage.recess = this@Attendee.recess.value.toBigDecimal()
            wage.recessOvertime = this@Attendee.recessOvertime.value.toBigDecimal()
        }
    }

    fun mergeDuplicates() = attendances.removeAllRevertable((0 until attendances.lastIndex)
            .filter { index -> Period(attendances[index], attendances[index + 1]).toStandardMinutes() < minutes(5) }
            .map { index -> attendances[index] })

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"
}