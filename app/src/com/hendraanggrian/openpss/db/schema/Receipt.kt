package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
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
}

data class Receipt(
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    val note: String,
    val paid: Double,
    val printed: Boolean
) : Document<Receipts> {
    override lateinit var id: Id<String, Receipts>
}