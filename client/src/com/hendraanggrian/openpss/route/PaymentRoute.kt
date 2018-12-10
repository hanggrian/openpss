package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

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
}