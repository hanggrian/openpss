package com.wijayaprinting.db.schema

import com.wijayaprinting.db.dao.Receipt
import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema

object Receipts : DocumentSchema<Receipt>("plate_receipt", Receipt::class) {
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)

    val dateTime = dateTime("date_time")
    val note = string("note")
    val paid = double("paid")
    val printed = boolean("printed")
}