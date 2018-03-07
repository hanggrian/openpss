package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.time.START_OF_TIME
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.ui.wage.Record.Companion.INDEX_NODE
import com.hendraanggrian.openpss.ui.wage.Record.Companion.INDEX_TOTAL
import com.hendraanggrian.openpss.util.round
import kotlinfx.beans.binding.doubleBindingOf
import kotlinfx.beans.property.toProperty
import org.joda.time.DateTime.now

fun Attendee.toNodeRecord(resourced: Resourced): Record = Record(resourced, INDEX_NODE, this, now().toProperty(), now().toProperty())

fun Attendee.toChildRecords(resourced: Resourced): Set<Record> {
    val records = mutableSetOf<Record>()
    val iterator = attendances.iterator()
    var index = 0
    while (iterator.hasNext()) records.add(Record(resourced, index++, this, iterator.next().toProperty(), iterator.next().toProperty()))
    return records
}

fun Attendee.toTotalRecords(resourced: Resourced, children: Collection<Record>): Record = Record(resourced, INDEX_TOTAL, this, START_OF_TIME.toProperty(), START_OF_TIME.toProperty()).apply {
    children.map { it.dailyProperty }.toTypedArray().let { mains -> dailyProperty.bind(doubleBindingOf(*mains) { mains.map { it.value }.sum().round() }) }
    children.map { it.dailyIncomeProperty }.toTypedArray().let { mainIncomes -> dailyIncomeProperty.bind(doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum().round() }) }
    children.map { it.overtimeProperty }.toTypedArray().let { overtimes -> overtimeProperty.bind(doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum().round() }) }
    children.map { it.overtimeIncomeProperty }.toTypedArray().let { overtimeIncomes -> overtimeIncomeProperty.bind(doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum().round() }) }
    children.map { it.totalProperty }.toTypedArray().let { totals -> totalProperty.bind(doubleBindingOf(*totals) { totals.map { it.value }.sum().round() }) }
}