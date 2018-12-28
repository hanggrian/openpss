package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.Page
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface InvoiceApi : Api {

    suspend fun getInvoices(
        search: Int = 0,
        customer: String? = null,
        isPaid: Boolean? = null,
        isDone: Boolean? = null,
        date: Any? = null,
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

    suspend fun deleteInvoice(login: Employee, invoice: Invoice): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("invoices")
        body = invoice
        parameters("login" to login.name)
    }

    suspend fun getInvoice(id: Id<String, *>): Invoice = client.get {
        apiUrl("invoices/$id")
    }

    suspend fun editInvoice(
        invoice: Invoice,
        isPrinted: Boolean = invoice.isPrinted,
        isPaid: Boolean = invoice.isPaid,
        isDone: Boolean = invoice.isDone
    ): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("invoices/${invoice.id}")
        parameters(
            "isPrinted" to isPrinted,
            "isPaid" to isPaid,
            "isDone" to isDone
        )
    }

    suspend fun nextInvoice(): Int = client.get {
        apiUrl("invoices/next")
    }
}