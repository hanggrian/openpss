package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Payments
import org.joda.time.DateTime

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
        ): Payment = Payment(invoiceId, employeeId, dateTime, value, reference)

        fun gather(payments: List<Payment>, isCash: Boolean) = payments
            .filter { it.isCash() == isCash }
            .sumByDouble { it.value }
    }

    override lateinit var id: StringId<Payments>

    fun isCash(): Boolean = reference == null
}