package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.BaseOffset
import com.hendraanggrian.openpss.db.BaseOrder
import com.hendraanggrian.openpss.db.BasePlate
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.OrderListColumn
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

object Receipts : DocumentSchema<Receipt>("plate_receipt", Receipt::class) {
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
        val minAmount = integer("min_amount")
        val minPrice = double("min_price")
        val excessPrice = double("excess_price")
    }

    class PaymentColumn : ListColumn<Payment, Receipts>("payments", Payment::class) {
        val dateTime = dateTime("date_time")
        val value = string("value")
    }
}

data class Receipt @JvmOverloads constructor(
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    var plates: List<Plate>,
    var offsets: List<Offset>,
    val note: String,
    val total: Double,
    val payments: List<Payment> = listOf(),
    val printed: Boolean = false
) : Document<Receipts> {

    override lateinit var id: Id<String, Receipts>
}

data class Plate(
    val plate: String,
    override var title: String,
    override var qty: Int,
    override var price: Double,
    override var total: Double = qty * price
) : BaseOrder, BasePlate

data class Offset(
    val offset: String,
    override var title: String,
    override var qty: Int,
    override var minQty: Int,
    override var minPrice: Double,
    override var excessPrice: Double,
    override var total: Double = when {
        qty <= minQty -> minPrice
        else -> minPrice + ((qty - minQty) * excessPrice)
    }
) : BaseOrder, BaseOffset

data class Payment(
    val dateTime: DateTime,
    val value: Double
)