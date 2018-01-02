@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.data

import com.wijayaprinting.manager.utils.round
import javafx.beans.property.SimpleObjectProperty
import kotfx.bind
import kotfx.doubleBindingOf
import org.joda.time.DateTime

fun Attendee.toNodeRecord(): AttendanceRecord = AttendanceRecord(AttendanceRecord.TYPE_NODE, this, SimpleObjectProperty(DateTime.now()), SimpleObjectProperty(DateTime.now()))

fun Attendee.toChildRecords(): Set<AttendanceRecord> {
    val records = mutableSetOf<AttendanceRecord>()
    val iterator = attendances.iterator()
    while (iterator.hasNext()) {
        records.add(AttendanceRecord(AttendanceRecord.TYPE_CHILD, this, SimpleObjectProperty(iterator.next()), SimpleObjectProperty(iterator.next())))
    }
    return records
}

fun Attendee.toTotalRecords(children: Collection<AttendanceRecord>): AttendanceRecord = AttendanceRecord(AttendanceRecord.TYPE_TOTAL, this, SimpleObjectProperty(DateTime(0)), SimpleObjectProperty(DateTime(0))).apply {
    children.map { it.daily }.toTypedArray().let { mains ->
        daily bind doubleBindingOf(*mains) {
            mains.map { it.value }.sum().round
        }
    }
    children.map { it.dailyIncome }.toTypedArray().let { mainIncomes ->
        dailyIncome bind doubleBindingOf(*mainIncomes) {
            mainIncomes.map { it.value }.sum().round
        }
    }
    children.map { it.overtime }.toTypedArray().let { overtimes ->
        overtime bind doubleBindingOf(*overtimes) {
            overtimes.map { it.value }.sum().round
        }
    }
    children.map { it.overtimeIncome }.toTypedArray().let { overtimeIncomes ->
        overtimeIncome bind doubleBindingOf(*overtimeIncomes) {
            overtimeIncomes.map { it.value }.sum().round
        }
    }
    children.map { it.total }.toTypedArray().let { totals ->
        total bind doubleBindingOf(*totals) {
            totals.map { it.value }.sum().round
        }
    }
}