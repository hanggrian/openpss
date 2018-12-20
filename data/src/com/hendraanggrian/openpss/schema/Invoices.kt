package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Invoice
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Invoices : DocumentSchema<Invoice>("invoices", Invoice::class) {
    val no = integer("no")
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)
    val dateTime = dateTime("date_time")
    val digitalJobs = DigitalJobs()
    val offsetJobs = OffsetJobs()
    val plateJobs = PlateJobs()
    val otherJobs = OtherJobs()
    val note = string("note")
    val isPrinted = boolean("is_printed")
    val isPaid = boolean("is_paid")
    val isDone = boolean("is_done")

    class DigitalJobs : ListColumn<Invoice.DigitalJob, Invoices>("digital_jobs", Invoice.DigitalJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val isTwoSide = boolean("two_side")
    }

    class OffsetJobs : ListColumn<Invoice.OffsetJob, Invoices>("offset_jobs", Invoice.OffsetJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val technique = string("technique")
    }

    class PlateJobs : ListColumn<Invoice.PlateJob, Invoices>("plate_jobs", Invoice.PlateJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
    }

    class OtherJobs : ListColumn<Invoice.OtherJob, Invoices>("other_jobs", Invoice.OtherJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
    }
}