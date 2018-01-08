package com.wijayaprinting.data

import com.wijayaprinting.dao.Recess
import com.wijayaprinting.dao.Wage
import com.wijayaprinting.Resourced
import com.wijayaprinting.internal.RevertableObservableList
import com.wijayaprinting.utils.safeTransaction
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import kotfx.mutableObservableListOf
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

        val recesses: ObservableList<Recess> = mutableObservableListOf(),

        /** Attendances and shift should be set in [com.wijayaprinting.manager.controller.AttendanceController]. */
        val attendances: RevertableObservableList<DateTime> = RevertableObservableList(),

        /** Wages below are retrieved from sql, or dailyEmpty if there is none. */
        val dailyProperty: IntegerProperty = SimpleIntegerProperty(),
        val hourlyOvertimeProperty: IntegerProperty = SimpleIntegerProperty()
) : Resourced by resourced {

    companion object {
        /** Dummy for invisible [javafx.scene.control.TreeTableView] root. */
        fun getDummy(resourced: Resourced): Attendee = Attendee(resourced, 0, "")
    }

    init {
        safeTransaction { Wage.findById(id) }?.let { wage ->
            dailyProperty.value = wage.daily
            hourlyOvertimeProperty.value = wage.hourlyOvertime
        }
    }

    var daily: Int
        get() = dailyProperty.get()
        set(value) = dailyProperty.set(value)

    var hourlyOvertime: Int
        get() = hourlyOvertimeProperty.get()
        set(value) = hourlyOvertimeProperty.set(value)

    fun saveWage() = safeTransaction {
        val wage = Wage.findById(id)
        if (wage == null) Wage.new(id) {
            daily = dailyProperty.value
            hourlyOvertime = hourlyOvertimeProperty.value
        } else {
            wage.daily = dailyProperty.value
            wage.hourlyOvertime = hourlyOvertimeProperty.value
        }
    }

    fun mergeDuplicates() = attendances.removeAllRevertable((0 until attendances.lastIndex)
            .filter { index -> Period(attendances[index], attendances[index + 1]).toStandardMinutes() < minutes(5) }
            .map { index -> attendances[index] })

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"
}