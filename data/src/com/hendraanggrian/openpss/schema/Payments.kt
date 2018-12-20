package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Payment
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.nullableString

object Payments : DocumentSchema<Payment>("payments", Payment::class) {
    val invoiceId = id("invoice_id", Invoices)
    val employeeId = id("employee_id", Employees)
    val dateTime = dateTime("date_time")
    val value = double("value")
    val reference = nullableString("reference")
}