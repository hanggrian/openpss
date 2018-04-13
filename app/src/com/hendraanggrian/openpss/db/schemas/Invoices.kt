package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Priced
import com.hendraanggrian.openpss.db.SplitPriced
import com.hendraanggrian.openpss.db.Totaled
import com.hendraanggrian.openpss.db.Typed
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.db.DateTimed
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
        val total = double("total")
    }

    class OffsetColumn : ListColumn<Offset, Invoices>("offsets", Offset::class) {
        val title = string("title")
        val qty = integer("qty")
        val type = string("type")
        val minQty = integer("min_qty")
        val minPrice = double("min_price")
        val excessPrice = double("excess_price")
        val total = double("total")
    }

    class OtherColumn : ListColumn<Other, Invoices>("others", Other::class) {
        val title = string("title")
        val qty = integer("qty")
        val price = double("price")
        val total = double("total")
    }
}

data class Invoice(
    val no: Int,
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    override val dateTime: DateTime,
    var plates: List<Plate>,
    var offsets: List<Offset>,
    var others: List<Other>,
    var note: String,
    var printed: Boolean,
    var paid: Boolean,
    var done: Boolean
) : Document<Invoices>, DateTimed, Totaled {

    override lateinit var id: Id<String, Invoices>

    override val total: Double
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
    override var title: String,
    override var qty: Int,
    override var type: String,
    override var price: Double,
    override var total: Double
) : Typed, Order, Priced {

    companion object {
        fun new(
            title: String,
            qty: Int,
            type: String,
            price: Double
        ): Plate = Plate(title, qty, type, price, qty * price)
    }
}

data class Offset(
    override var title: String,
    override var qty: Int,
    override var type: String,
    override var minQty: Int,
    override var minPrice: Double,
    override var excessPrice: Double,
    override var total: Double
) : Typed, Order, SplitPriced {

    companion object {
        fun new(
            title: String,
            qty: Int,
            type: String,
            minQty: Int,
            minPrice: Double,
            excessPrice: Double
        ): Offset = Offset(title, qty, type, minQty, minPrice, excessPrice,
            calculateTotal(qty, minQty, minPrice, excessPrice))

        fun calculateTotal(
            qty: Int,
            minQty: Int,
            minPrice: Double,
            excessPrice: Double
        ): Double = when {
            qty <= minQty -> minPrice
            else -> minPrice + ((qty - minQty) * excessPrice)
        }
    }
}

data class Other(
    override var title: String,
    override var qty: Int,
    override var price: Double,
    override var total: Double
) : Order, Priced {

    companion object {
        fun new(
            title: String,
            qty: Int,
            price: Double
        ): Other = Other(title, qty, price, qty * price)
    }
}