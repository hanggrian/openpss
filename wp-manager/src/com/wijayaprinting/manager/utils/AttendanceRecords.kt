@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.utils

import com.wijayaprinting.manager.data.AttendanceRecord
import com.wijayaprinting.manager.data.Attendee
import kotfx.asMutableProperty
import kotfx.asProperty
import kotfx.bind
import kotfx.doubleBindingOf
import org.joda.time.DateTime
import org.joda.time.DateTime.now

inline fun Attendee.toRootRecord(): AttendanceRecord = AttendanceRecord(this, AttendanceRecord.TYPE_ROOT, this, DateTime(0).asProperty(), DateTime(0).asProperty())

inline fun Attendee.toNodeRecord(): AttendanceRecord = AttendanceRecord(this, AttendanceRecord.TYPE_NODE, this, now().asProperty(), now().asProperty())

inline fun Attendee.toChildRecords(): Set<AttendanceRecord> {
    val records = mutableSetOf<AttendanceRecord>()
    val iterator = attendances.iterator()
    while (iterator.hasNext()) records.add(AttendanceRecord(this, AttendanceRecord.TYPE_CHILD, this, iterator.next().asMutableProperty(), iterator.next().asMutableProperty()))
    return records
}

inline fun Attendee.toTotalRecords(children: Collection<AttendanceRecord>): AttendanceRecord = AttendanceRecord(this, AttendanceRecord.TYPE_TOTAL, this, DateTime(0).asProperty(), DateTime(0).asProperty()).apply {
    children.map { it.dailyProperty }.toTypedArray().let { mains ->
        dailyProperty bind doubleBindingOf(*mains) { mains.map { it.value }.sum().round }
    }
    children.map { it.dailyIncomeProperty }.toTypedArray().let { mainIncomes ->
        dailyIncomeProperty bind doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum().round }
    }
    children.map { it.overtimeProperty }.toTypedArray().let { overtimes ->
        overtimeProperty bind doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum().round }
    }
    children.map { it.overtimeIncomeProperty }.toTypedArray().let { overtimeIncomes ->
        overtimeIncomeProperty bind doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum().round }
    }
    children.map { it.totalProperty }.toTypedArray().let { totals ->
        totalProperty bind doubleBindingOf(*totals) { totals.map { it.value }.sum().round }
    }
}