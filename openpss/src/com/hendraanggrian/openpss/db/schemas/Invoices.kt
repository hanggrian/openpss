package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Resources
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Numbered
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.util.enumValueOfId
import com.hendraanggrian.openpss.util.id
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import org.joda.time.DateTime

object Invoices : DocumentSchema<Invoice>("invoices", Invoice::class) {
    val no = integer("no")
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)
    val dateTime = dateTime("date_time")
    val offsets = Offsets()
    val digitals = Digitals()
    val plates = Plates()
    val others = Others()
    val note = string("note")
    val printed = boolean("printed")
    val paid = boolean("paid")
    val done = boolean("done")

    class Offsets : ListColumn<Invoice.Offset, Invoices>("offsets", Invoice.Offset::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
        val machine = string("machine")
        val technique = string("technique")
    }

    class Digitals : ListColumn<Invoice.Digital, Invoices>("digitals", Invoice.Digital::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
        val machine = string("machine")
        val technique = string("technique")
    }

    class Plates : ListColumn<Invoice.Plate, Invoices>("plates", Invoice.Plate::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
        val machine = string("machine")
    }

    class Others : ListColumn<Invoice.Other, Invoices>("others", Invoice.Other::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
    }
}

data class Invoice(
    override val no: Int,
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    var offsets: List<Offset>,
    var plates: List<Plate>,
    var others: List<Other>,
    var note: String,
    val printed: Boolean,
    val paid: Boolean,
    val done: Boolean
) : Document<Invoices>, Numbered {

    companion object {

        fun new(
            employeeId: Id<String, Employees>,
            customerId: Id<String, Customers>,
            dateTime: DateTime,
            offsets: List<Offset>,
            plates: List<Plate>,
            others: List<Other>,
            note: String
        ): Invoice = Invoice(
            Numbered.next(Invoices),
            employeeId, customerId, dateTime, offsets, plates, others, note, false, false, false
        )
    }

    override lateinit var id: Id<String, Invoices>

    val total: Double get() = plates.sum() + offsets.sum() + others.sum()

    private fun List<Order>.sum() = sumByDouble { it.total }

    data class Offset(
        override val qty: Int,
        override val title: String,
        override val total: Double,
        val machine: String,
        val technique: String
    ) : Titled, Order {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                machine: String,
                technique: Technique
            ): Offset = Offset(qty, title, total, machine, technique.id)
        }

        val typedTechnique: Technique get() = enumValueOfId(technique)

        enum class Technique : Resources.Enum {
            ONE_SIDE {
                override val resourceId: String = R.string.one_side
            },
            TWO_SIDE_EQUAL {
                override val resourceId: String = R.string.two_side_equal
            },
            TWO_SIDE_DISTINCT {
                override val resourceId: String = R.string.two_side_distinct
            }
        }
    }

    data class Digital(
        override val qty: Int,
        override val title: String,
        override val total: Double,
        val oneSidePrice: Int,
        val twoSidePrice: Int
    ) : Titled, Order {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                oneSidePrice: Int,
                twoSidePrice: Int
            ): Digital = Digital(qty, title, total, oneSidePrice, twoSidePrice)
        }
    }

    data class Plate(
        override val qty: Int,
        override val title: String,
        override val total: Double,
        val machine: String
    ) : Titled, Order {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                machine: String
            ): Plate = Plate(qty, title, total, machine)
        }
    }

    data class Other(
        override val qty: Int,
        override val title: String,
        override val total: Double
    ) : Titled, Order {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double
            ): Invoice.Other = Invoice.Other(qty, title, total)
        }
    }

    interface Order {
        val qty: Int
        val title: String
        val total: Double
    }
}