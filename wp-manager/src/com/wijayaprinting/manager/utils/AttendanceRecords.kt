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

inline fun Attendee.toNodeRecord(): AttendanceRecord = AttendanceRecord(AttendanceRecord.TYPE_NODE, this, now().asProperty(), now().asProperty())

inline fun Attendee.toChildRecords(): Set<AttendanceRecord> {
    val records = mutableSetOf<AttendanceRecord>()
    val iterator = attendances.iterator()
    while (iterator.hasNext()) records.add(AttendanceRecord(AttendanceRecord.TYPE_CHILD, this, iterator.next().asMutableProperty(), iterator.next().asMutableProperty()))
    return records
}

inline fun Attendee.toTotalRecords(children: Collection<AttendanceRecord>): AttendanceRecord = AttendanceRecord(AttendanceRecord.TYPE_TOTAL, this, DateTime(0).asProperty(), DateTime(0).asProperty()).apply {
    children.map { it.daily }.toTypedArray().let { mains ->
        daily bind doubleBindingOf(*mains) { mains.map { it.value }.sum().round }
    }
    children.map { it.dailyIncome }.toTypedArray().let { mainIncomes ->
        dailyIncome bind doubleBindingOf(*mainIncomes) { mainIncomes.map { it.value }.sum().round }
    }
    children.map { it.overtime }.toTypedArray().let { overtimes ->
        overtime bind doubleBindingOf(*overtimes) { overtimes.map { it.value }.sum().round }
    }
    children.map { it.overtimeIncome }.toTypedArray().let { overtimeIncomes ->
        overtimeIncome bind doubleBindingOf(*overtimeIncomes) { overtimeIncomes.map { it.value }.sum().round }
    }
    children.map { it.total }.toTypedArray().let { totals ->
        total bind doubleBindingOf(*totals) { totals.map { it.value }.sum().round }
    }
}