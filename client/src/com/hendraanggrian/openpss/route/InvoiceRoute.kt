package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface InvoiceRoute : Route {

    suspend fun getInvoices(
        search: Int,
        customer: String?,
        isPaid: Boolean?,
        date: Any?,
        page: Int,
        count: Int
    ): Page<Invoice> = client.get {
        apiUrl("invoices")
        parameters(
            "search" to search,
            "customer" to customer,
            "isPaid" to isPaid,
            "date" to date,
            "page" to page,
            "count" to count
        )
    }

    suspend fun addInvoice(invoice: Invoice): Invoice = client.post {
        apiUrl("invoices")
        body = invoice
    }

    suspend fun getInvoice(no: Int): Invoice = client.get {
        apiUrl("invoices/$no")
    }

    suspend fun editInvoice(
        invoice: Invoice,
        isPrinted: Boolean = invoice.isPrinted,
        isPaid: Boolean = invoice.isPaid,
        isDone: Boolean = invoice.isDone
    ): Boolean = client.requestStatus {
        apiUrl("invoices/${invoice.no}")
        parameters(
            "isPrinted" to isPrinted,
            "isPaid" to isPaid,
            "isDone" to isDone
        )
    }

    suspend fun deleteInvoice(login: Employee, invoice: Invoice): Boolean = client.requestStatus {
        apiUrl("invoices/${invoice.no}")
        method = HttpMethod.Delete
        body = invoice
        parameters("login" to login.name)
    }

    suspend fun nextInvoice(): Int = client.get {
        apiUrl("invoices/next")
    }
}