package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.dbDateTime
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.nullableString
import org.joda.time.DateTime

object Payments : DocumentSchema<Payment>("payments", Payment::class) {
    val invoiceId = id("invoice_id", Invoices)
    val employeeId = id("employee_id", Employees)
    val dateTime = dateTime("date_time")
    val value = double("value")
    val reference = nullableString("reference")
}

data class Payment(
    var invoiceId: Id<String, Invoices>,
    var employeeId: Id<String, Employees>,
    val dateTime: DateTime,
    val value: Double,
    val reference: String?
) : Document<Payments> {

    companion object {
        fun new(
            invoiceId: Id<String, Invoices>,
            employeeId: Id<String, Employees>,
            value: Double,
            reference: String?
        ): Payment = Payment(invoiceId, employeeId, dbDateTime, value, reference)

        fun gather(payments: List<Payment>, isCash: Boolean) = payments
            .filter { it.isCash() == isCash }
            .sumByDouble { it.value }
    }

    override lateinit var id: Id<String, Payments>

    fun isCash(): Boolean = reference == null
}