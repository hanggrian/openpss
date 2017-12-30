@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.data

import com.wijayaprinting.manager.utils.round
import javafx.beans.property.SimpleObjectProperty
import kotfx.bind
import kotfx.doubleBindingOf
import org.joda.time.DateTime

fun Employee.toNodeRecord(): Record = Record(Record.TYPE_NODE, this, SimpleObjectProperty(DateTime.now()), SimpleObjectProperty(DateTime.now()))

fun Employee.toChildRecords(): Set<Record> {
    val records = mutableSetOf<Record>()
    val iterator = attendances.iterator()
    while (iterator.hasNext()) {
        records.add(Record(Record.TYPE_CHILD, this, SimpleObjectProperty(iterator.next()), SimpleObjectProperty(iterator.next())))
    }
    return records
}

fun Employee.toTotalRecords(childs: Collection<Record>): Record = Record(Record.TYPE_TOTAL, this, SimpleObjectProperty(DateTime(0)), SimpleObjectProperty(DateTime(0))).apply {
    childs.map { it.daily }.toTypedArray().let { mains ->
        daily bind doubleBindingOf(*mains) {
            mains.map { it.value }.sum().round
        }
    }
    childs.map { it.dailyIncome }.toTypedArray().let { mainIncomes ->
        dailyIncome bind doubleBindingOf(*mainIncomes) {
            mainIncomes.map { it.value }.sum().round
        }
    }
    childs.map { it.overtime }.toTypedArray().let { overtimes ->
        overtime bind doubleBindingOf(*overtimes) {
            overtimes.map { it.value }.sum().round
        }
    }
    childs.map { it.overtimeIncome }.toTypedArray().let { overtimeIncomes ->
        overtimeIncome bind doubleBindingOf(*overtimeIncomes) {
            overtimeIncomes.map { it.value }.sum().round
        }
    }
    childs.map { it.total }.toTypedArray().let { totals ->
        total bind doubleBindingOf(*totals) {
            totals.map { it.value }.sum().round
        }
    }
}