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
        val dateTime = dateTime("date_time")
        val value = double("value")
    }
}

data class Receipt @JvmOverloads constructor(
    val dateTime: DateTime /*= START_OF_TIME*/,
    var plates: List<Plate> /*= listOf()*/,
    var offsets: List<Offset> /*= listOf()*/,
    var note: String /*= ""*/,
    var total: Double /*= 0.0*/,
    var payments: List<Payment> = listOf(),
    var printed: Boolean = false
) : Document<Receipts> {

    override lateinit var id: Id<String, Receipts>
    lateinit var employeeId: Id<String, Employees>
    lateinit var customerId: Id<String, Customers>

    fun isPaid(): Boolean = payments.sumByDouble { it.value } >= total
}

data class Plate(
    var plate: String,
    override var title: String,
    override var qty: Int,
    override var price: Double,
    override var total: Double = qty * price
) : BaseOrder, BasePlate

data class Offset(
    var offset: String,
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
    val dateTime: DateTime = dbDateTime,
    var value: Double
) {

    lateinit var employeeId: Id<String, Employees>
}