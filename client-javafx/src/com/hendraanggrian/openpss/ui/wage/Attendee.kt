package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.StringResources
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.data.Recess
import com.hendraanggrian.openpss.data.Wage
import com.hendraanggrian.openpss.ui.wage.record.Record
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.INDEX_NODE
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.INDEX_TOTAL
import com.hendraanggrian.openpss.util.START_OF_TIME
import com.hendraanggrian.openpss.util.round
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import ktfx.bindings.buildDoubleBinding
import ktfx.collections.mutableObservableListOf
import ktfx.getValue
import ktfx.setValue
import ktfx.toProperty
import org.joda.time.DateTime
import org.joda.time.Minutes.minutes
import org.joda.time.Period

/** Data class representing an Attendee with id as its identifier to avoid duplicates in [Set] scenario. */
data class Attendee(
    /** Id and name are final value that should be determined upon xlsx reading. */
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

    suspend fun init(api: OpenPSSApi) {
        api.getWage(id)?.let { wage ->
            daily = wage.daily
            hourlyOvertime = wage.hourlyOvertime
        }
        // merge duplicates
        attendances.removeAllRevertible((0 until attendances.lastIndex)
            .filter { index ->
                Period(
                    attendances[index],
                    attendances[index + 1]
                ).toStandardMinutes() < minutes(5)
            }
            .map { index -> attendances[index] })
    }

    suspend fun saveWage(api: OpenPSSApi) = api.getWages().let { wages ->
        when {
            wages.isEmpty() -> api.addWage(Wage(id, daily, hourlyOvertime))
            else -> wages.single().let { wage ->
                if (wage.daily != daily || wage.hourlyOvertime != hourlyOvertime) {
                    api.editWage(wage.also {
                        it.daily = daily
                        it.hourlyOvertime = hourlyOvertime
                    })
                }
            }
        }
    }

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean = other != null && other is Attendee && other.id == id

    override fun toString(): String = "$id. $name"

    fun toNodeRecord(resources: StringResources): Record = Record(
        resources,
        INDEX_NODE,
        this,
        DateTime.now().toProperty(),
        DateTime.now().toProperty()
    )

    fun toChildRecords(resources: StringResources): Set<Record> {
        val records = mutableSetOf<Record>()
        val iterator = attendances.iterator()
        var index = 0
        while (iterator.hasNext()) records += Record(
            resources,
            index++,
            this,
            iterator.next().toProperty(),
            iterator.next().toProperty()
        )
        return records
    }

    fun toTotalRecords(resources: StringResources, children: Collection<Record>): Record =
        Record(resources, INDEX_TOTAL, this, START_OF_TIME.toProperty(), START_OF_TIME.toProperty())
            .apply {
                dailyProperty.bind(buildDoubleBinding(children.map { it.dailyProperty }) {
                    children.sumByDouble { it.daily }.round()
                })
                dailyIncomeProperty.bind(buildDoubleBinding(children.map { it.dailyIncomeProperty }) {
                    children.sumByDouble { it.dailyIncome }.round()
                })
                overtimeProperty.bind(buildDoubleBinding(children.map { it.overtimeProperty }) {
                    children.sumByDouble { it.overtime }.round()
                })
                overtimeIncomeProperty.bind(buildDoubleBinding(children.map { it.overtimeIncomeProperty }) {
                    children.sumByDouble { it.overtimeIncome }.round()
                })
                totalProperty.bind(buildDoubleBinding(children.map { it.totalProperty }) {
                    children.sumByDouble { it.total }.round()
                })
            }

    companion object {

        /** Dummy for invisible [javafx.scene.control.TreeTableView] rootLayout. */
        val DUMMY: Attendee = Attendee(0, "")
    }
}