package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Wage
import com.hendraanggrian.openpss.db.schemas.Wages
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.wage.record.Record
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.INDEX_NODE
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.INDEX_TOTAL
import com.hendraanggrian.openpss.util.START_OF_TIME
import com.hendraanggrian.openpss.util.isEmpty
import com.hendraanggrian.openpss.util.round
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.binding.doubleBindingOf
import ktfx.beans.property.toProperty
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.collections.mutableObservableListOf
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period

/** Data class representing an Attendee with id as its identifier to avoid duplicates in [Set] scenario. */
data class Attendee(
    /** Id and name are final values that should be determined upon xlsx reading. */
    val id: Int,
    val name: String,
    val role: String? = null,

    val recesses: ObservableList<Recess> = mutableObservableListOf(),

    /** Attendances and shift should be set in [com.hendraanggrian.openpss.ui.wage.AttendeePane]. */
    val attendances: RevertibleObservableList<DateTime> = RevertibleObservableList(),

    /** Wages below are retrieved from sql, or dailyEmpty if there is none. */
    val dailyProperty: IntegerProperty = SimpleIntegerProperty(),
    val hourlyOvertimeProperty: IntegerProperty = SimpleIntegerProperty()
) {

    var daily: Int by dailyProperty
    var hourlyOvertime: Int by hourlyOvertimeProperty

    init {
        transaction {
            Wages { it.wageId.equal(id) }.singleOrNull()?.let { wage ->
                daily = wage.daily
                hourlyOvertime = wage.hourlyOvertime
            }
        }
    }

    fun saveWage() {
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            Wages { it.wageId.equal(id) }.let { wages ->
                if (wages.isEmpty()) Wages += Wage(id, daily, hourlyOvertime)
                else wages.single().let { wage ->
                    when {
                        wage.daily != daily && wage.hourlyOvertime != hourlyOvertime ->
                            wages.projection { daily + hourlyOvertime }.update(daily, hourlyOvertime)
                        wage.daily != daily -> wages.projection { daily }.update(daily)
                        else -> wages.projection { hourlyOvertime }.update(hourlyOvertime)
                    }
                }
            }
        }
    }

    fun mergeDuplicates() = attendances.removeAllRevertible((0 until attendances.lastIndex)
        .filter { index -> Period(attendances[index], attendances[index + 1]).toStandardMinutes() < minutes(5) }
        .map { index -> attendances[index] })

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"

    fun toNodeRecord(resourced: Resourced): Record =
        Record(resourced, INDEX_NODE, this, DateTime.now().toProperty(), DateTime.now().toProperty())

    fun toChildRecords(resourced: Resourced): Set<Record> {
        val records = mutableSetOf<Record>()
        val iterator = attendances.iterator()
        var index = 0
        while (iterator.hasNext()) records +=
            Record(resourced, index++, this, iterator.next().toProperty(), iterator.next().toProperty())
        return records
    }

    fun toTotalRecords(resourced: Resourced, children: Collection<Record>): Record =
        Record(resourced, INDEX_TOTAL, this, START_OF_TIME.toProperty(), START_OF_TIME.toProperty()).apply {
            dailyProperty.bind(doubleBindingOf(*children.map { it.dailyProperty }.toTypedArray()) {
                children.sumByDouble { it.daily }.round()
            })
            dailyIncomeProperty.bind(doubleBindingOf(*children.map { it.dailyIncomeProperty }.toTypedArray()) {
                children.sumByDouble { it.dailyIncome }.round()
            })
            overtimeProperty.bind(doubleBindingOf(*children.map { it.overtimeProperty }.toTypedArray()) {
                children.sumByDouble { it.overtime }.round()
            })
            overtimeIncomeProperty.bind(doubleBindingOf(*children.map { it.overtimeIncomeProperty }.toTypedArray()) {
                children.sumByDouble { it.overtimeIncome }.round()
            })
            totalProperty.bind(doubleBindingOf(*children.map { it.totalProperty }.toTypedArray()) {
                children.sumByDouble { it.total }.round()
            })
        }

    companion object {
        /** Dummy for invisible [javafx.scene.control.TreeTableView] root. */
        val DUMMY = Attendee(0, "")
    }
}