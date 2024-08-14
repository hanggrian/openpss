package com.hanggrian.openpss.db.schemas

import com.hanggrian.openpss.R
import com.hanggrian.openpss.Resources
import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.Numbered
import com.hanggrian.openpss.util.enumValueOfId
import com.hanggrian.openpss.util.id
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
    val digitalJobs = DigitalJobs()
    val offsetJobs = OffsetJobs()
    val plateJobs = PlateJobs()
    val otherJobs = OtherJobs()
    val note = string("note")
    val printed = boolean("printed")
    val isPaid = boolean("is_paid")
    val isDone = boolean("is_done")

    class DigitalJobs :
        ListColumn<Invoice.DigitalJob, Invoices>("digital_jobs", Invoice.DigitalJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val isTwoSide = boolean("two_side")
    }

    class OffsetJobs :
        ListColumn<Invoice.OffsetJob, Invoices>("offset_jobs", Invoice.OffsetJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
        val technique = string("technique")
    }

    class PlateJobs :
        ListColumn<Invoice.PlateJob, Invoices>("plate_jobs", Invoice.PlateJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
        val type = string("type")
    }

    class OtherJobs :
        ListColumn<Invoice.OtherJob, Invoices>("other_jobs", Invoice.OtherJob::class) {
        val qty = integer("qty")
        val desc = string("desc")
        val total = string("total")
    }
}

data class Invoice(
    override val no: Int,
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    var digitalJobs: List<DigitalJob>,
    var offsetJobs: List<OffsetJob>,
    var plateJobs: List<PlateJob>,
    var otherJobs: List<OtherJob>,
    var note: String,
    val isPrinted: Boolean,
    val isPaid: Boolean,
    val isDone: Boolean,
) : Document<Invoices>,
    Numbered {
    override lateinit var id: Id<String, Invoices>

    val jobs: List<Job>
        get() {
            val jobs = mutableListOf<Job>()
            digitalJobs.forEach { jobs += it }
            offsetJobs.forEach { jobs += it }
            plateJobs.forEach { jobs += it }
            otherJobs.forEach { jobs += it }
            return jobs
        }

    inline val total: Double get() = jobs.sumByDouble { it.total }

    companion object {
        fun new(
            employeeId: Id<String, Employees>,
            customerId: Id<String, Customers>,
            dateTime: DateTime,
            digitalJobs: List<DigitalJob>,
            offsetJobs: List<OffsetJob>,
            plateJobs: List<PlateJob>,
            otherJobs: List<OtherJob>,
            note: String,
        ): Invoice =
            Invoice(
                Numbered.next(Invoices),
                employeeId,
                customerId,
                dateTime,
                digitalJobs,
                offsetJobs,
                plateJobs,
                otherJobs,
                note,
                false,
                false,
                false,
            )

        fun no(resources: Resources, no: Number?): String? =
            no?.let { "${resources.getString(R.string_invoice)} #$it" }
    }

    data class DigitalJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String,
        val isTwoSide: Boolean,
    ) : TypedJob {
        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                type: String,
                isTwoSide: Boolean,
            ): DigitalJob = DigitalJob(qty, title, total, type, isTwoSide)
        }
    }

    data class OffsetJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String,
        val technique: String,
    ) : TypedJob {
        val typedTechnique: Technique get() = enumValueOfId(technique)

        companion object {
            fun new(
                qty: Int,
                title: String,
                total: Double,
                type: String,
                technique: Technique,
            ): OffsetJob = OffsetJob(qty, title, total, type, technique.id)
        }

        enum class Technique : Resources.Enum {
            ONE_SIDE {
                override val resourceId: String = R.string_one_side
            },
            TWO_SIDE_EQUAL {
                override val resourceId: String = R.string_two_side_equal
            },
            TWO_SIDE_DISTINCT {
                override val resourceId: String = R.string_two_side_distinct
            },
        }
    }

    data class PlateJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String,
    ) : TypedJob {
        companion object {
            fun new(qty: Int, title: String, total: Double, type: String): PlateJob =
                PlateJob(qty, title, total, type)
        }
    }

    data class OtherJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
    ) : Job {
        companion object {
            fun new(qty: Int, title: String, total: Double): OtherJob = OtherJob(qty, title, total)
        }
    }

    interface TypedJob : Job {
        val type: String
    }

    interface Job {
        val qty: Int
        val desc: String
        val total: Double
    }
}
