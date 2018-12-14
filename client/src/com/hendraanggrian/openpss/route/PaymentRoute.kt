package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface PaymentRoute : Route {

    suspend fun addPayment(payment: Payment): Invoice = client.post {
        apiUrl("payments")
        body = payment
    }

    suspend fun deletePayment(login: Employee, payment: Payment): Boolean = client.requestStatus {
        apiUrl("payments")
        method = HttpMethod.Delete
        body = payment
        parameters("login" to login.name)
    }

    suspend fun getPayments(invoiceId: Id<String, *>): List<Payment> = client.get {
        apiUrl("payments/$invoiceId")
    }

    suspend fun getPaymentDue(invoiceId: Id<String, *>): Double = client.get {
        apiUrl("payments/$invoiceId/due")
    }
}