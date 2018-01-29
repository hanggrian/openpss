package com.wijayaprinting.ui.wage

import com.wijayaprinting.START_OF_TIME
import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.ui.wage.Record.Companion.INDEX_NODE
import com.wijayaprinting.ui.wage.Record.Companion.INDEX_TOTAL
import com.wijayaprinting.util.rounded
import kotfx.doubleBindingOf
import kotfx.toMutableProperty
import kotfx.toProperty
import org.joda.time.DateTime.now

fun Attendee.toNodeRecord(resourced: Resourced): Record = Record(resourced, INDEX_NODE, this, now().toProperty(), now().toProperty())

fun Attendee.toChildRecords(resourced: Resourced): Set<Record> {
    val records = mutableSetOf<Record>()
    val iterator = attendances.iterator()
    var index = 0
    while (iterator.hasNext()) records.add(Record(resourced, index++, this, iterator.next().toMutableProperty(), iterator.next().toMutableProperty()))
    return records
}

fun Attendee.toTotalRecords(resourced: Resourced, children: Collection<Record>): Record = Record(resourced, INDEX_TOTAL, this, START_OF_TIME.toProperty(), START_OF_TIME.toProperty()).apply {
    children.map { it.dailyProperty }.toTypedArray().let { mains -> dailyProperty.bind(doubleBindingOf(*mains) { mains.map { it.value }.sum().rounded }) }
    children.map { it.dailyIncomeProperty }.toTypedArray().let { mainIncomes -> dailyIncomeProperty.bind(doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum().rounded }) }
    children.map { it.overtimeProperty }.toTypedArray().let { overtimes -> overtimeProperty.bind(doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum().rounded }) }
    children.map { it.overtimeIncomeProperty }.toTypedArray().let { overtimeIncomes -> overtimeIncomeProperty.bind(doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum().rounded }) }
    children.map { it.totalProperty }.toTypedArray().let { totals -> totalProperty.bind(doubleBindingOf(*totals) { totals.map { it.value }.sum().rounded }) }
}