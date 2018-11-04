package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Resources
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Numbered
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.util.enumValueOfId
import com.hendraanggrian.openpss.util.id
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import org.joda.time.DateTime

object Invoices : DocumentSchema<Invoice>("invoices", Invoice::class) {
    val no = integer("no")
    val employeeId = id("employee_id", Employees)
    val customerId = id("customer_id", Customers)
    val dateTime = dateTime("date_time")
    val offset_jobs = OffsetJobs()
    val digital_jobs = DigitalJobs()
    val plate_jobs = PlateJobs()
    val other_jobs = OtherJobs()
    val note = string("note")
    val printed = boolean("printed")
    val paid = boolean("paid")
    val done = boolean("done")

    class OffsetJobs : ListColumn<Invoice.OffsetJob, Invoices>("offsets", Invoice.OffsetJob::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
        val machine = string("machine")
        val technique = string("technique")
    }

    class DigitalJobs : ListColumn<Invoice.DigitalJob, Invoices>("digitals", Invoice.DigitalJob::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
        val machine = string("machine")
        val technique = string("technique")
    }

    class PlateJobs : ListColumn<Invoice.PlateJob, Invoices>("plates", Invoice.PlateJob::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
        val machine = string("machine")
    }

    class OtherJobs : ListColumn<Invoice.OtherJob, Invoices>("others", Invoice.OtherJob::class) {
        val qty = integer("qty")
        val title = string("title")
        val total = string("total")
    }
}

data class Invoice(
    override val no: Int,
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    var offsetJobs: List<OffsetJob>,
    var digitalJobs: List<DigitalJob>,
    var plateJobs: List<PlateJob>,
    var otherJobs: List<OtherJob>,
    var note: String,
    val printed: Boolean,
    val paid: Boolean,
    val done: Boolean
) : Document<Invoices>, Numbered {

    companion object {

        fun new(
            employeeId: Id<String, Employees>,
            customerId: Id<String, Customers>,
            dateTime: DateTime,
            offsetJobs: List<OffsetJob>,
            digitalJobs: List<DigitalJob>,
            plateJobs: List<PlateJob>,
            otherJobs: List<OtherJob>,
            note: String
        ): Invoice = Invoice(
            Numbered.next(Invoices),
            employeeId, customerId, dateTime, offsetJobs, digitalJobs, plateJobs, otherJobs, note, false, false, false
        )
    }

    override lateinit var id: Id<String, Invoices>

    val jobs: List<Job>
        get() {
            val list = mutableListOf<Job>()
            offsetJobs.forEach { list += it }
            digitalJobs.forEach { list += it }
            plateJobs.forEach { list += it }
            otherJobs.forEach { list += it }
            return list
        }

    val total: Double get() = jobs.sumByDouble { it.total }

    data class OffsetJob(
        override val qty: Int,
        override val title: String,
        override val total: Double,
        val machine: String,
        val technique: String
    ) : Titled, Job {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                machine: String,
                technique: Technique
            ): OffsetJob = OffsetJob(qty, title, total, machine, technique.id)
        }

        val typedTechnique: Technique get() = enumValueOfId(technique)

        enum class Technique : Resources.Enum {
            ONE_SIDE {
                override val resourceId: String = R.string.one_side
            },
            TWO_SIDE_EQUAL {
                override val resourceId: String = R.string.two_side_equal
            },
            TWO_SIDE_DISTINCT {
                override val resourceId: String = R.string.two_side_distinct
            }
        }
    }

    data class DigitalJob(
        override val qty: Int,
        override val title: String,
        override val total: Double,
        val oneSidePrice: Int,
        val twoSidePrice: Int
    ) : Titled, Job {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                oneSidePrice: Int,
                twoSidePrice: Int
            ): DigitalJob = DigitalJob(qty, title, total, oneSidePrice, twoSidePrice)
        }
    }

    data class PlateJob(
        override val qty: Int,
        override val title: String,
        override val total: Double,
        val machine: String
    ) : Titled, Job {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                machine: String
            ): PlateJob = PlateJob(qty, title, total, machine)
        }
    }

    data class OtherJob(
        override val qty: Int,
        override val title: String,
        override val total: Double
    ) : Titled, Job {

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double
            ): Invoice.OtherJob = Invoice.OtherJob(qty, title, total)
        }
    }

    interface Job {
        val qty: Int
        val title: String
        val total: Double
    }
}