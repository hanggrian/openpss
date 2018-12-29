package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Payments
import kotlinx.nosql.Id
import org.joda.time.DateTime

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
            dateTime: DateTime,
            value: Double,
            reference: String? = null
        ): Payment = Payment(invoiceId, employeeId, dateTime, value, reference)

        fun gather(payments: List<Payment>, isCash: Boolean) = payments
            .filter { it.isCash() == isCash }
            .sumByDouble { it.value }
    }

    override lateinit var id: Id<String, Payments>

    fun isCash(): Boolean = reference == null
}