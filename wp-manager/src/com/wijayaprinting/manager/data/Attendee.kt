package com.wijayaprinting.manager.data

import com.wijayaprinting.dao.Wage
import com.wijayaprinting.manager.Resourced
import com.wijayaprinting.manager.internal.RevertableObservableList
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period

/** Data class representing an Attendee with id as its identifier to avoid duplicates in [Set] scenario. */
data class Attendee @JvmOverloads constructor(
        val resourced: Resourced,

        /** Id and name are final values that should be determined upon xlsx reading. */
        val id: Int,
        val name: String,
        val role: String? = null,

        /** Attendances and shift should be set in [com.wijayaprinting.manager.controller.AttendanceController]. */
        val attendances: RevertableObservableList<DateTime> = RevertableObservableList(),

        /** Wages below are retrieved from sql, or dailyEmpty if there is none. */
        val dailyProperty: IntegerProperty = SimpleIntegerProperty(),
        val hourlyOvertimeProperty: IntegerProperty = SimpleIntegerProperty(),
        val recessProperty: DoubleProperty = SimpleDoubleProperty(),
        val recessOvertimeProperty: DoubleProperty = SimpleDoubleProperty()
) : Resourced by resourced {

    companion object {
        /** Dummy for invisible [javafx.scene.control.TreeTableView] root. */
        fun getDummy(resourced: Resourced): Attendee = Attendee(resourced, 0, "")
    }

    init {
        safeTransaction { Wage.findById(id) }?.let { wage ->
            dailyProperty.value = wage.daily
            hourlyOvertimeProperty.value = wage.hourlyOvertime
            recessProperty.value = wage.recess.toDouble()
            recessOvertimeProperty.value = wage.recessOvertime.toDouble()
        }
    }

    fun saveWage() = safeTransaction {
        val wage = Wage.findById(id)
        if (wage == null) Wage.new(id) {
            daily = dailyProperty.value
            hourlyOvertime = hourlyOvertimeProperty.value
            recess = recessProperty.value.toBigDecimal()
            recessOvertime = recessOvertimeProperty.value.toBigDecimal()
        } else {
            wage.daily = dailyProperty.value
            wage.hourlyOvertime = hourlyOvertimeProperty.value
            wage.recess = recessProperty.value.toBigDecimal()
            wage.recessOvertime = recessOvertimeProperty.value.toBigDecimal()
        }
    }

    fun mergeDuplicates() = attendances.removeAllRevertable((0 until attendances.lastIndex)
            .filter { index -> Period(attendances[index], attendances[index + 1]).toStandardMinutes() < minutes(5) }
            .map { index -> attendances[index] })

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"
}