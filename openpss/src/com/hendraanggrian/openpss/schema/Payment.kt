package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.Schema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.nullableString
import org.joda.time.DateTime

object Payments : Schema<Payment>("payments", Payment::class) {
    val invoiceId = id("invoice_id", Invoices)
    val employeeId = id("employee_id", Employees)
    val dateTime = dateTime("date_time")
    val value = double("value")
    val reference = nullableString("reference")
}

data class Payment(
    var invoiceId: StringId<Invoices>,
    var employeeId: StringId<Employees>,
    val dateTime: DateTime,
    val value: Double,
    val reference: String?
) : Document<Payments> {

    companion object {

        fun new(
            invoiceId: StringId<Invoices>,
            employeeId: StringId<Employees>,
            dateTime: DateTime,
            value: Double,
            reference: String? = null
        ): Payment = Payment(
            invoiceId,
            employeeId,
            dateTime,
            value,
            reference
        )

        fun gather(payments: List<Payment>, isCash: Boolean) = payments
            .filter { it.isCash() == isCash }
            .sumByDouble { it.value }
    }

    override lateinit var id: StringId<Payments>

    fun isCash(): Boolean = reference == null
}
