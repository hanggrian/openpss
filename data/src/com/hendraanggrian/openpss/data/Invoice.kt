package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.Customers
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.Invoices
import kotlinx.nosql.Id
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
data class Invoice(
    /**
     * Since `id` is reserved in [Document], `no` is direct replacement.
     * Basically means the same thing.
     */
    val no: Int,
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
    val isDone: Boolean
) : Document<Invoices> {

    companion object {

        fun new(
            no: Int,
            employeeId: Id<String, Employees>,
            customerId: Id<String, Customers>,
            dateTime: DateTime,
            digitalJobs: List<DigitalJob>,
            offsetJobs: List<OffsetJob>,
            plateJobs: List<PlateJob>,
            otherJobs: List<OtherJob>,
            note: String
        ): Invoice = Invoice(
            no,
            employeeId, customerId, dateTime, digitalJobs, offsetJobs, plateJobs, otherJobs, note, false, false, false
        )
    }

    override lateinit var id: Id<String, Invoices>

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

    @Serializable
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

    @Serializable
    data class OffsetJob(
        override val qty: Int,
        override val desc: String,
        override val total: Double,
        override val type: String,
        val technique: String
    ) : JobWithType {

        companion object
    }

    @Serializable
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

    @Serializable
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