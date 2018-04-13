package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.utils.findById
import javafx.collections.ObservableList
import ktfx.collections.toObservableList

data class Schedule(
    val type: Type,
    val employee: String,
    val customer: String,
    val title: String
) {
    companion object {
        /** Invoices that aren't done yet. */
        fun from(invoices: Iterable<Invoice>): ObservableList<Schedule> = invoices
            .groupBy {
                transaction {
                    findById(Employees, it.employeeId).single().name to findById(Customers, it.customerId).single().name
                }!!
            }
            .flatMap({ (pair, invoices) ->
                val (employee, customer) = pair
                invoices.flatMap { it.plates }.map { Schedule(Type.PLATE, employee, customer, it.title) } +
                    invoices.flatMap { it.offsets }.map { Schedule(Type.OFFSET, employee, customer, it.title) } +
                    invoices.flatMap { it.others }.map { Schedule(Type.OTHER, employee, customer, it.title) }
            })
            .toObservableList()
    }

    enum class Type {
        PLATE, OFFSET, OTHER
    }
}