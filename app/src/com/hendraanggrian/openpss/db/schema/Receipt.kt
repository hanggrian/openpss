package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.BaseOffset
import com.hendraanggrian.openpss.db.BaseOrder
import com.hendraanggrian.openpss.db.BasePlate
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.OrderListColumn
import com.hendraanggrian.openpss.db.dbDateTime
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
    val note = string("note")
    val total = double("total")
    val payments = PaymentColumn()
    val printed = boolean("printed")

    class PlateColumn : OrderListColumn<Plate, Receipts>("plates", Plate::class) {
        val plate = string("plate")
        val price = double("price")
    }

    class OffsetColumn : OrderListColumn<Offset, Receipts>("offsets", Offset::class) {
        val offset = string("offset")
        val minQty = integer("min_qty")
        val minPrice = double("min_price")
        val excessPrice = double("excess_price")
    }

    class PaymentColumn : ListColumn<Payment, Receipts>("payments", Payment::class) {
        val employeeId = id("employee_id", Employees)
        val value = double("value")
        val dateTime = dateTime("date_time")
    }
}

data class Receipt(
    val dateTime: DateTime,
    var plates: List<Plate>,
    var offsets: List<Offset>,
    var note: String,
    var total: Double,
    var payments: List<Payment>,
    var printed: Boolean
) : Document<Receipts> {

    override lateinit var id: Id<String, Receipts>
    lateinit var employeeId: Id<String, Employees>
    lateinit var customerId: Id<String, Customers>

    fun isPaid(): Boolean = payments.sumByDouble { it.value } >= total

    companion object {
        fun new(
            dateTime: DateTime,
            plates: List<Plate>,
            offsets: List<Offset>,
            note: String,
            total: Double,
            payments: List<Payment> = listOf(),
            printed: Boolean = false
        ): Receipt = Receipt(dateTime, plates, offsets, note, total, payments, printed)
    }
}

data class Plate(
    var plate: String,
    override var title: String,
    override var qty: Int,
    override var price: Double,
    override var total: Double
) : BaseOrder, BasePlate {

    companion object {
        fun new(
            plate: String,
            title: String,
            qty: Int,
            price: Double,
            total: Double = qty * price
        ): Plate = Plate(plate, title, qty, price, total)
    }
}

data class Offset(
    var offset: String,
    override var title: String,
    override var qty: Int,
    override var minQty: Int,
    override var minPrice: Double,
    override var excessPrice: Double,
    override var total: Double
) : BaseOrder, BaseOffset {

    companion object {
        fun new(
            offset: String,
            title: String,
            qty: Int,
            minQty: Int,
            minPrice: Double,
            excessPrice: Double,
            total: Double = when {
                qty <= minQty -> minPrice
                else -> minPrice + ((qty - minQty) * excessPrice)
            }
        ): Offset = Offset(offset, title, qty, minQty, minPrice, excessPrice, total)
    }
}

data class Payment(
    var value: Double,
    val dateTime: DateTime
) {

    lateinit var employeeId: Id<String, Employees>

    companion object {
        fun new(
            value: Double,
            dateTime: DateTime = dbDateTime
        ): Payment = Payment(value, dateTime)
    }
}