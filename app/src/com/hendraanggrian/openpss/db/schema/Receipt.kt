package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.BaseOffset
import com.hendraanggrian.openpss.db.BaseOrder
import com.hendraanggrian.openpss.db.BasePlate
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.OrderListColumn
import kotlinx.nosql.Id
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
    val note = string("note")
    val paid = double("paid")
    val printed = boolean("printed")

    val plates = PlateColumn()
    val offsets = OffsetColumn()

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
}

data class Receipt(
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    val note: String,
    val paid: Double,
    val printed: Boolean,
    var plates: List<Plate> = listOf(),
    var offsets: List<Offset> = listOf()
) : Document<Receipts> {

    override lateinit var id: Id<String, Receipts>
}

data class Plate(
    override var title: String,
    val plate: String,
    override var qty: Int,
    override var price: Double,
    override var total: Double = qty * price
) : BaseOrder, BasePlate

data class Offset(
    override var title: String,
    val offset: String,
    override var qty: Int,
    override var minQty: Int,
    override var minPrice: Double,
    override var excessPrice: Double,
    override var total: Double = when {
        qty <= minQty -> minPrice
        else -> minPrice + ((qty - minQty) * excessPrice)
    }
) : BaseOrder, BaseOffset