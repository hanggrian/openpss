package com.wijayaprinting.db

import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema
import org.joda.time.DateTime

object Receipts : DocumentSchema<Receipt>("plate_receipt", Receipt::class) {
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)

    val dateTime = dateTime("date_time")
    val note = string("note")
    val paid = double("paid")
    val printed = boolean("printed")
}

data class Receipt(
        val employeeId: Id<String, Employees>,
        val customerId: Id<String, Customers>,
        val dateTime: DateTime,
        val note: String,
        val paid: Double,
        val printed: Boolean
) {
    lateinit var id: Id<String, Receipts>

    /*val total: Double
        get() {
            var sum= 0.0

        }*/
}

/*
object Receipts : IntIdTable("plate_receipt") {
    val datetime = datetime("datetime")
    // val employee = reference("employee", Employees).nullable()
    val customer = reference("customer", Customers)
    val note = varchar("note", 100).default("")
    val paid = decimal("price", 15, 2).default(ZERO)
    val printed = bool("printed").default(false)
}

class Receipt(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Receipt>(Receipts)

    val orders by PlateOrder referrersOn PlateOrders.receipt

    var datetime by Receipts.datetime
    // var employee by Employee optionalReferencedOn Receipts.employee
    var customer by Customer referencedOn Receipts.customer
    var note by Receipts.note
    var paid by Receipts.paid
    var printed by Receipts.printed

    val total: BigDecimal
        get() {
            var sum: BigDecimal = ZERO
            for (order in orders) sum += order.total
            return sum
        }

    val isPaid: Boolean get() = total == paid
}*/
