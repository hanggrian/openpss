package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.Payment
import com.hendraanggrian.openpss.schema.Payments
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface PaymentApi : Api {

    suspend fun getPayments(dateTime: Any): List<Payment> = client.get {
        apiUrl(Payments.schemaName)
        parameters("dateTime" to dateTime)
    }

    suspend fun addPayment(payment: Payment): Invoice = client.post {
        apiUrl(Payments.schemaName)
        jsonBody(payment)
    }

    suspend fun deletePayment(login: Employee, payment: Payment): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl(Payments.schemaName)
        jsonBody(payment)
        parameters("login" to login.name)
    }

    suspend fun getPayments(invoiceId: Id<String, *>): List<Payment> = client.get {
        apiUrl("${Payments.schemaName}/$invoiceId")
    }

    suspend fun getPaymentDue(invoiceId: Id<String, *>): Double = client.get {
        apiUrl("${Payments.schemaName}/$invoiceId/due")
    }
}