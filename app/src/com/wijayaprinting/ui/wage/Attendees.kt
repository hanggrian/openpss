package com.wijayaprinting.ui.wage

import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.util.rounded
import kotfx.asMutableProperty
import kotfx.asProperty
import kotfx.bind
import kotfx.doubleBindingOf
import org.joda.time.DateTime

fun Attendee.toNodeRecord(resourced: Resourced): Record = Record(resourced, Record.INDEX_NODE, this, DateTime.now().asProperty(), DateTime.now().asProperty())

fun Attendee.toChildRecords(resourced: Resourced): Set<Record> {
    val records = mutableSetOf<Record>()
    val iterator = attendances.iterator()
    var index = 0
    while (iterator.hasNext()) records.add(Record(resourced, index++, this, iterator.next().asMutableProperty(), iterator.next().asMutableProperty()))
    return records
}

fun Attendee.toTotalRecords(resourced: Resourced, children: Collection<Record>): Record = Record(resourced, Record.INDEX_TOTAL, this, DateTime(0).asProperty(), DateTime(0).asProperty()).apply {
    children.map { it.dailyProperty }.toTypedArray().let { mains -> dailyProperty bind doubleBindingOf(*mains) { mains.map { it.value }.sum().rounded } }
    children.map { it.dailyIncomeProperty }.toTypedArray().let { mainIncomes -> dailyIncomeProperty bind doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum().rounded } }
    children.map { it.overtimeProperty }.toTypedArray().let { overtimes -> overtimeProperty bind doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum().rounded } }
    children.map { it.overtimeIncomeProperty }.toTypedArray().let { overtimeIncomes -> overtimeIncomeProperty bind doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum().rounded } }
    children.map { it.totalProperty }.toTypedArray().let { totals -> totalProperty bind doubleBindingOf(*totals) { totals.map { it.value }.sum().rounded } }
}