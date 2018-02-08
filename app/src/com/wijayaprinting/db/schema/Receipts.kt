package com.wijayaprinting.db.schema

import com.wijayaprinting.db.dao.Receipt
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Receipts : DocumentSchema<Receipt>("plate_receipt", Receipt::class) {
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)

    val dateTime = dateTime("date_time")
    val note = string("note")
    val paid = double("paid")
    val printed = boolean("printed")
}