package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Priced
import com.hendraanggrian.openpss.db.SplitPriced
import com.hendraanggrian.openpss.db.Totaled
import com.hendraanggrian.openpss.db.Typed
import com.hendraanggrian.openpss.ui.DateTimed
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

object Receipts : DocumentSchema<Receipt>("receipts", Receipt::class) {
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)
    val dateTime = dateTime("date_time")
    val plates = PlateColumn()
    val offsets = OffsetColumn()
    val others = OtherColumn()
    val note = string("note")
    val paid = boolean("paid")
    val printed = boolean("printed")

    class PlateColumn : ListColumn<Plate, Receipts>("plates", Plate::class) {
        val type = string("type")
        val title = string("title")
        val qty = integer("qty")
        val price = double("price")
        val total = double("total")
    }

    class OffsetColumn : ListColumn<Offset, Receipts>("offsets", Offset::class) {
        val type = string("type")
        val title = string("title")
        val qty = integer("qty")
        val minQty = integer("min_qty")
        val minPrice = double("min_price")
        val excessPrice = double("excess_price")
        val total = double("total")
    }

    class OtherColumn : ListColumn<Other, Receipts>("others", Other::class) {
        val title = string("title")
        val qty = integer("qty")
        val price = double("price")
        val total = double("total")
    }
}

data class Receipt(
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    override val dateTime: DateTime,
    var plates: List<Plate>,
    var offsets: List<Offset>,
    var others: List<Other>,
    var note: String,
    var paid: Boolean,
    var printed: Boolean
) : Document<Receipts>, DateTimed, Totaled {

    override lateinit var id: Id<String, Receipts>

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
        ): Receipt = Receipt(employeeId, customerId, dateTime, plates, offsets, others, note, false, false)
    }
}

data class Plate(
    override var type: String,
    override var title: String,
    override var qty: Int,
    override var price: Double,
    override var total: Double
) : Typed, Order, Priced {

    companion object {
        fun new(
            type: String,
            title: String,
            qty: Int,
            price: Double
        ): Plate = Plate(type, title, qty, price, qty * price)
    }
}

data class Offset(
    override var type: String,
    override var title: String,
    override var qty: Int,
    override var minQty: Int,
    override var minPrice: Double,
    override var excessPrice: Double,
    override var total: Double
) : Typed, Order, SplitPriced {

    companion object {
        fun new(
            type: String,
            title: String,
            qty: Int,
            minQty: Int,
            minPrice: Double,
            excessPrice: Double
        ): Offset = Offset(type, title, qty, minQty, minPrice, excessPrice, when {
            qty <= minQty -> minPrice
            else -> minPrice + ((qty - minQty) * excessPrice)
        })
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
            price: Double,
            total: Double = qty * price
        ): Other = Other(title, qty, price, total)
    }
}