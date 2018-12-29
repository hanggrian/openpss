package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.nosql.NamedSchemed
import com.hendraanggrian.openpss.nosql.Schema
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.integer
import kotlinx.nosql.string

object Invoices : Schema<Invoice>(Invoices, Invoice::class) {
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

    override fun toString(): String = "invoices"

    class DigitalJobs : ListColumn<Invoice.DigitalJob, Invoices>("${Invoices.DigitalJobs}", Invoice.DigitalJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val isTwoSide = boolean("two_side")

        companion object : NamedSchemed {
            override fun toString(): String = "digital_jobs"
        }
    }

    class OffsetJobs : ListColumn<Invoice.OffsetJob, Invoices>("${Invoices.OffsetJobs}", Invoice.OffsetJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val technique = string("technique")

        companion object : NamedSchemed {
            override fun toString(): String = "offset_jobs"
        }
    }

    class PlateJobs : ListColumn<Invoice.PlateJob, Invoices>("${Invoices.PlateJobs}", Invoice.PlateJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")

        companion object : NamedSchemed {
            override fun toString(): String = "plate_jobs"
        }
    }

    class OtherJobs : ListColumn<Invoice.OtherJob, Invoices>("${Invoices.OtherJobs}", Invoice.OtherJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")

        companion object : NamedSchemed {
            override fun toString(): String = "other_jobs"
        }
    }
}