package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.OffsetOrder
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.SimpleOrder
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.StringResource
import com.hendraanggrian.openpss.util.enumValueOfId
import com.hendraanggrian.openpss.util.id
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
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
    val plates = Plates()
    val offsets = Offsets()
    val others = Others()
    val note = string("note")
    val printed = boolean("printed")
    val paid = boolean("paid")
    val done = boolean("done")

    class Plates : ListColumn<Invoice.Plate, Invoices>("plates", Invoice.Plate::class) {
        val title = string("title")
        val qty = integer("qty")
        val machine = string("machine")
        val price = double("price")
    }

    class Offsets : ListColumn<Invoice.Offset, Invoices>("offsets", Invoice.Offset::class) {
        val title = string("title")
        val qty = integer("qty")
        val machine = string("machine")
        val technique = string("technique")
        val minQty = integer("min_qty")
        val minPrice = double("min_price")
        val excessPrice = double("excess_price")
    }

    class Others : ListColumn<Invoice.Other, Invoices>("others", Invoice.Other::class) {
        val title = string("title")
        val qty = integer("qty")
        val price = double("price")
    }
}

data class Invoice(
    val no: Int,
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    var plates: List<Plate>,
    var offsets: List<Offset>,
    var others: List<Other>,
    var note: String,
    val printed: Boolean,
    val paid: Boolean,
    val done: Boolean
) : Document<Invoices> {
    companion object {
        fun new(
            employeeId: Id<String, Employees>,
            customerId: Id<String, Customers>,
            dateTime: DateTime,
            plates: List<Plate>,
            offsets: List<Offset>,
            others: List<Other>,
            note: String
        ): Invoice = Invoice(
            transaction { Invoices.find().lastOrNull()?.no ?: 0 } + 1,
            employeeId, customerId, dateTime, plates, offsets, others, note, false, false, false)
    }

    override lateinit var id: Id<String, Invoices>

    val total: Double get() = plates.sum() + offsets.sum() + others.sum()

    private fun List<Order>.sum() = sumByDouble { it.total }

    data class Plate(
        override val title: String,
        override val qty: Int,
        val machine: String,
        override val price: Double
    ) : Titled, SimpleOrder {
        companion object {
            fun new(
                title: String,
                qty: Int,
                machine: String,
                price: Double
            ): Plate = Plate(title, qty, machine, price)
        }
    }

    data class Offset(
        override val title: String,
        override val qty: Int,
        val machine: String,
        val technique: String,
        override val minQty: Int,
        override val minPrice: Double,
        override val excessPrice: Double
    ) : Titled, OffsetOrder {
        companion object {
            fun new(
                title: String,
                qty: Int,
                machine: String,
                technique: Technique,
                minQty: Int,
                minPrice: Double,
                excessPrice: Double
            ): Offset = Offset(title, qty, machine, technique.id, minQty, minPrice, excessPrice)
        }

        override val typedTechnique: Technique get() = enumValueOfId(technique)

        enum class Technique : StringResource {
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

    data class Other(
        override val title: String,
        override val qty: Int,
        override val price: Double
    ) : Titled, SimpleOrder {
        companion object {
            fun new(
                title: String,
                qty: Int,
                price: Double
            ): Invoice.Other = Invoice.Other(title, qty, price)
        }
    }
}