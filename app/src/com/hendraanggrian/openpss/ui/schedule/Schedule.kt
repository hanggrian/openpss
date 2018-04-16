package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.utils.numberConverter

data class Schedule(
    val firstColumn: String,
    val title: String,
    val qty: String = ""
) {

    constructor(
        firstColumn: String,
        title: String,
        qty: Int) : this(firstColumn, title, numberConverter.toString(qty))

    /*companion object {
        */
    /** Invoices that aren't done yet. *//*
        fun listAll(invoices: Iterable<Invoice>, type: Type?): ObservableList<Schedule> = invoices
            .groupBy {
                transaction {
                    Triple(it.id,
                        findById(Employees, it.employeeId).single().name,
                        findById(Customers, it.customerId).single().name)
                }!!
            }
            .flatMap({ (pair, invoices) ->
                val (id, employee, customer) = pair
                val _type = type ?: ANY
                var list = listOf<Schedule>()
                if (_type == ANY || _type == PLATE) list += invoices.flatMap { it.plates }.map {
                    Schedule(id, it, PLATE, employee, customer)
                }
                if (_type == ANY || _type == OFFSET) list += invoices.flatMap { it.offsets }.map {
                    Schedule(id, it, OFFSET, employee, customer)
                }
                if (_type == ANY || _type == OTHER) list += invoices.flatMap { it.others }.map {
                    Schedule(id, it, OTHER, employee, customer)
                }
                list
            })
            .toObservableList()
    }

    val description: String
        get() = when (type) {
            ANY -> ""
            OTHER -> ""
            else -> "${order.qty}"
        }

    enum class Type {
        ANY, PLATE, OFFSET, OTHER;

        fun asString(resourced: Resourced): String = resourced.getString(when (this) {
            ANY -> R.string.any
            PLATE -> R.string.plate
            OFFSET -> R.string.offset
            OTHER -> R.string.others
        })
    }*/
}