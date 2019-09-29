package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.Schema
import com.hendraanggrian.openpss.nosql.Schemed
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.integer
import kotlinx.nosql.string
import org.joda.time.DateTime

object Invoices : Schema<Invoice>("invoices", Invoice::class) {
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

    class DigitalJobs :
        ListColumn<Invoice.DigitalJob, Invoices>(Invoices.DigitalJobs.schemaName, Invoice.DigitalJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val isTwoSide = boolean("two_side")

        companion object : Schemed {
            override val schemaName: String = "digital_jobs"
        }
    }

    class OffsetJobs :
        ListColumn<Invoice.OffsetJob, Invoices>(Invoices.OffsetJobs.schemaName, Invoice.OffsetJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val technique = string("technique")

        companion object : Schemed {
            override val schemaName: String = "offset_jobs"
        }
    }

    class PlateJobs : ListColumn<Invoice.PlateJob, Invoices>(Invoices.PlateJobs.schemaName, Invoice.PlateJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")

        companion object : Schemed {
            override val schemaName: String = "plate_jobs"
        }
    }

    class OtherJobs : ListColumn<Invoice.OtherJob, Invoices>(Invoices.OtherJobs.schemaName, Invoice.OtherJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")

        companion object : Schemed {
            override val schemaName: String = "other_jobs"
        }
    }
}

data class Invoice(
    /**
     * Since `id` is reserved in [Document], `no` is direct replacement.
     * Basically means the same thing.
     */
    val no: Int,
    val employeeId: StringId<Employees>,
    val customerId: StringId<Customers>,
    val dateTime: DateTime,
    var digitalJobs: List<DigitalJob>,
    var offsetJobs: List<OffsetJob>,
    var plateJobs: List<PlateJob>,
    var otherJobs: List<OtherJob>,
    var note: String,
    var isPrinted: Boolean,
    var isPaid: Boolean,
    var isDone: Boolean
) : Document<Invoices> {

    companion object {

        fun new(
            no: Int,
            employeeId: StringId<Employees>,
            customerId: StringId<Customers>,
            dateTime: DateTime,
            digitalJobs: List<DigitalJob>,
            offsetJobs: List<OffsetJob>,
            plateJobs: List<PlateJob>,
            otherJobs: List<OtherJob>,
            note: String
        ): Invoice = Invoice(
            no, employeeId, customerId, dateTime, digitalJobs, offsetJobs, plateJobs, otherJobs,
            note, false, false, false
        )
    }

    override lateinit var id: StringId<Invoices>

    val jobs: List<Job>
        get() {
            val list = mutableListOf<Job>()
            digitalJobs.forEach { list += it }
            offsetJobs.forEach { list += it }
            plateJobs.forEach { list += it }
            otherJobs.forEach { list += it }
            return list
        }

    inline val total: Double get() = jobs.sumByDouble { it.total }

    data class DigitalJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String,
        val isTwoSide: Boolean
    ) : JobWithType {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                type: String,
                isTwoSide: Boolean
            ): DigitalJob = DigitalJob(qty, title, total, type, isTwoSide)
        }
    }

    data class OffsetJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String,
        val technique: String
    ) : JobWithType {

        companion object
    }

    data class PlateJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String
    ) : JobWithType {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                type: String
            ): PlateJob = PlateJob(qty, title, total, type)
        }
    }

    data class OtherJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double
    ) : Job {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double
            ): Invoice.OtherJob = Invoice.OtherJob(qty, title, total)
        }
    }

    interface JobWithType : Job {
        val type: String
    }

    interface Job {
        val qty: Int
        val desc: String
        val total: Double
    }
}
