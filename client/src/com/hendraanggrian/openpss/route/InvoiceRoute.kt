package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.content.Page
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
    }

    suspend fun deleteInvoice(invoice: Invoice): Boolean = client.requestStatus {
        apiUrl("invoices/${invoice.no}")
        method = HttpMethod.Delete
        body = invoice
    }
}