package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.Resources
import com.hanggrian.openpss.db.schemas.Recess
import com.hanggrian.openpss.db.schemas.Wage
import com.hanggrian.openpss.db.schemas.Wages
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.ui.wage.record.Record
import com.hanggrian.openpss.ui.wage.record.Record.Companion.INDEX_NODE
import com.hanggrian.openpss.ui.wage.record.Record.Companion.INDEX_TOTAL
import com.hanggrian.openpss.util.START_OF_TIME
import com.hanggrian.openpss.util.isEmpty
import com.hanggrian.openpss.util.round
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.bindings.doubleBindingOf
import ktfx.collections.mutableObservableListOf
import ktfx.getValue
import ktfx.propertyOf
import ktfx.setValue
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period

/**
 * Data class representing an Attendee with id as its identifier to avoid duplicates in [Set]
 * scenario.
 */
data class Attendee(
    /** Id and name are final value that should be determined upon xlsx reading. */
    val id: Int,
    val name: String,
    val role: String? = null,
    val recesses: ObservableList<Recess> = mutableObservableListOf(),
    /** Attendances and shift should be set in [com.hanggrian.openpss.ui.wage.AttendeePane]. */
    val attendances: RevertibleObservableList<DateTime> = RevertibleObservableList(),
    /** Wages below are retrieved from sql, or dailyEmpty if there is none. */
    val dailyProperty: IntegerProperty = SimpleIntegerProperty(),
    val hourlyOvertimeProperty: IntegerProperty = SimpleIntegerProperty(),
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
        transaction {
            Wages { it.wageId.equal(id) }.let { wages ->
                if (wages.isEmpty()) {
                    Wages += Wage(id, daily, hourlyOvertime)
                    return@let
                }
                wages.single().let { wage ->
                    when {
                        wage.daily != daily && wage.hourlyOvertime != hourlyOvertime ->
                            wages
                                .projection { daily + hourlyOvertime }
                                .update(daily, hourlyOvertime)
                        wage.daily != daily ->
                            wages
                                .projection { daily }
                                .update(daily)
                        else ->
                            wages
                                .projection { hourlyOvertime }
                                .update(hourlyOvertime)
                    }
                }
            }
        }
    }

    fun mergeDuplicates() =
        attendances.removeAllRevertible(
            (0 until attendances.lastIndex)
                .filter { index ->
                    Period(attendances[index], attendances[index + 1])
                        .toStandardMinutes() < minutes(5)
                }.map { index -> attendances[index] },
        )

    fun toNodeRecord(resources: Resources): Record =
        Record(resources, INDEX_NODE, this, propertyOf(DateTime.now()), propertyOf(DateTime.now()))

    fun toChildRecords(resources: Resources): Set<Record> {
        val records = mutableSetOf<Record>()
        val iterator = attendances.iterator()
        var index = 0
        while (iterator.hasNext()) {
            records +=
                Record(
                    resources,
                    index++,
                    this,
                    propertyOf(iterator.next()),
                    propertyOf(iterator.next()),
                )
        }
        return records
    }

    fun toTotalRecords(resources: Resources, children: Collection<Record>): Record =
        Record(resources, INDEX_TOTAL, this, propertyOf(START_OF_TIME), propertyOf(START_OF_TIME))
            .apply {
                dailyProperty.bind(
                    doubleBindingOf(children.map { it.dailyProperty }) {
                        children.sumByDouble { it.daily }.round()
                    },
                )
                dailyIncomeProperty.bind(
                    doubleBindingOf(children.map { it.dailyIncomeProperty }) {
                        children.sumByDouble { it.dailyIncome }.round()
                    },
                )
                overtimeProperty.bind(
                    doubleBindingOf(children.map { it.overtimeProperty }) {
                        children.sumByDouble { it.overtime }.round()
                    },
                )
                overtimeIncomeProperty.bind(
                    doubleBindingOf(children.map { it.overtimeIncomeProperty }) {
                        children.sumByDouble { it.overtimeIncome }.round()
                    },
                )
                totalProperty.bind(
                    doubleBindingOf(children.map { it.totalProperty }) {
                        children.sumByDouble { it.total }.round()
                    },
                )
            }

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"

    companion object {
        /** Dummy for invisible [javafx.scene.control.TreeTableView] root. */
        val DUMMY = Attendee(0, "")
    }
}
