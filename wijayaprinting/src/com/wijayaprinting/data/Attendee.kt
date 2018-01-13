package com.wijayaprinting.data

import com.wijayaprinting.collections.RevertableObservableList
import com.wijayaprinting.core.Resourced
import com.wijayaprinting.nosql.Recess
import com.wijayaprinting.nosql.Wage
import com.wijayaprinting.nosql.Wages
import com.wijayaprinting.nosql.transaction
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import kotfx.mutableObservableListOf
import kotlinx.nosql.equal
import kotlinx.nosql.update
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
        transaction {
            Wages.find { wageId.equal(id) }.singleOrNull()?.let { wage ->
                dailyProperty.value = wage.daily
                hourlyOvertimeProperty.value = wage.hourlyOvertime
            }
        }
    }

    var daily: Int
        get() = dailyProperty.get()
        set(value) = dailyProperty.set(value)

    var hourlyOvertime: Int
        get() = hourlyOvertimeProperty.get()
        set(value) = hourlyOvertimeProperty.set(value)

    fun saveWage() {
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            Wages.find { wageId.equal(id) }.let {
                if (it.count() == 0) Wages.insert(Wage(id, daily, hourlyOvertime))
                else it.projection { wageId + daily + hourlyOvertime }.update(id, daily, hourlyOvertime)
            }
        }
    }

    fun mergeDuplicates() = attendances.removeAllRevertable((0 until attendances.lastIndex)
            .filter { index -> Period(attendances[index], attendances[index + 1]).toStandardMinutes() < minutes(5) }
            .map { index -> attendances[index] })

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"
}