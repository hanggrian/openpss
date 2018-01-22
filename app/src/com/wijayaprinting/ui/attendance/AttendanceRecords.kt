package com.wijayaprinting.ui.attendance

import com.wijayaprinting.util.rounded
import kotfx.asMutableProperty
import kotfx.asProperty
import kotfx.bind
import kotfx.doubleBindingOf
import org.joda.time.DateTime

fun Attendee.toNodeRecord(): AttendanceRecord = AttendanceRecord(AttendanceRecord.INDEX_NODE, this, DateTime.now().asProperty(), DateTime.now().asProperty())

fun Attendee.toChildRecords(): Set<AttendanceRecord> {
    val records = mutableSetOf<AttendanceRecord>()
    val iterator = attendances.iterator()
    var index = 0
    while (iterator.hasNext()) records.add(AttendanceRecord(index++, this, iterator.next().asMutableProperty(), iterator.next().asMutableProperty()))
    return records
}

fun Attendee.toTotalRecords(children: Collection<AttendanceRecord>): AttendanceRecord = AttendanceRecord(AttendanceRecord.INDEX_TOTAL, this, DateTime(0).asProperty(), DateTime(0).asProperty()).apply {
    children.map { it.dailyProperty }.toTypedArray().let { mains ->
        dailyProperty bind doubleBindingOf(*mains) { mains.map { it.value }.sum().rounded }
    }
    children.map { it.dailyIncomeProperty }.toTypedArray().let { mainIncomes ->
        dailyIncomeProperty bind doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum().rounded }
    }
    children.map { it.overtimeProperty }.toTypedArray().let { overtimes ->
        overtimeProperty bind doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum().rounded }
    }
    children.map { it.overtimeIncomeProperty }.toTypedArray().let { overtimeIncomes ->
        overtimeIncomeProperty bind doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum().rounded }
    }
    children.map { it.totalProperty }.toTypedArray().let { totals ->
        totalProperty bind doubleBindingOf(*totals) { totals.map { it.value }.sum().rounded }
    }
}