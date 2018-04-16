package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.SimpleOrder
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.transaction
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
    val plates = PlateColumn()
    val offsets = OffsetColumn()
    val others = OtherColumn()
    val note = string("note")
    val printed = boolean("printed")
    val paid = boolean("paid")
    val done = boolean("done")

    class PlateColumn : ListColumn<Plate, Invoices>("plates", Plate::class) {
        val title = string("title")
        val qty = integer("qty")
        val type = string("type")
        val price = double("price")
    }

    class OffsetColumn : ListColumn<Offset, Invoices>("offsets", Offset::class) {
        val title = string("title")
        val qty = integer("qty")
        val type = string("type")
        val minQty = integer("min_qty")
        val minPrice = double("min_price")
        val excessPrice = double("excess_price")
    }

    class OtherColumn : ListColumn<Other, Invoices>("others", Other::class) {
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
    val done:Boolean
) : Document<Invoices> {

    override lateinit var id: Id<String, Invoices>

    val total: Double
        get() = plates.sumByDouble { it.total } + offsets.sumByDouble { it.total } + others.sumByDouble { it.total }

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
            transaction { Invoices.find().lastOrNull()?.no ?: 0 }!! + 1,
            employeeId, customerId, dateTime, plates, offsets, others, note, false, false, false)
    }
}

data class Plate(
    override val title: String,
    override val qty: Int,
    val type: String,
    override val price: Double
) : SimpleOrder {

    companion object {
        fun new(
            title: String,
            qty: Int,
            type: String,
            price: Double
        ): Plate = Plate(title, qty, type, price)
    }
}

data class Offset(
    override val title: String,
    override val qty: Int,
    val type: String,
    val minQty: Int,
    val minPrice: Double,
    val excessPrice: Double
) : Order {

    override val total: Double
        get() = when {
            qty <= minQty -> minPrice
            else -> minPrice + ((qty - minQty) * excessPrice)
        }

    companion object {
        fun new(
            title: String,
            qty: Int,
            type: String,
            minQty: Int,
            minPrice: Double,
            excessPrice: Double
        ): Offset = Offset(title, qty, type, minQty, minPrice, excessPrice)
    }
}

data class Other(
    override val title: String,
    override val qty: Int,
    override val price: Double
) : SimpleOrder {

    companion object {
        fun new(
            title: String,
            qty: Int,
            price: Double
        ): Other = Other(title, qty, price)
    }
}