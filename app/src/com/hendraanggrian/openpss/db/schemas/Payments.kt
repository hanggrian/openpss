package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.resources.StringResource
import com.hendraanggrian.openpss.util.enumValueOfId
import com.hendraanggrian.openpss.util.id
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.nullableString
import kotlinx.nosql.string
import org.joda.time.DateTime

object Payments : DocumentSchema<Payment>("payments", Payment::class) {
    val invoiceId = id("invoice_id", Invoices)
    val employeeId = id("employee_id", Employees)
    val dateTime = dateTime("date_time")
    val method = string("method")
    val value = double("value")
    val transfer = nullableString("transfer")
}

data class Payment(
    var invoiceId: Id<String, Invoices>,
    var employeeId: Id<String, Employees>,
    val dateTime: DateTime,
    val method: String,
    val value: Double,
    val transfer: String?
) : Document<Payments> {
    companion object {
        fun new(
            invoiceId: Id<String, Invoices>,
            employeeId: Id<String, Employees>,
            method: Method,
            value: Double,
            transfer: String?
        ): Payment = Payment(invoiceId, employeeId, dbDateTime, method.id, value, transfer)

        fun gather(payments: List<Payment>, method: Payment.Method) = payments
            .filter { it.typedMethod == method }
            .sumByDouble { it.value }
    }

    override lateinit var id: Id<String, Payments>

    val typedMethod: Method get() = enumValueOfId(method)

    enum class Method : StringResource {
        CASH {
            override val resourceId: String = R.string.cash
        },
        TRANSFER {
            override val resourceId: String = R.string.transfer
        };
    }
}