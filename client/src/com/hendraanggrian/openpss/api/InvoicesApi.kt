package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.schema.Invoices
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface InvoicesApi : Api {

    suspend fun getInvoices(
        search: Int = 0,
        customer: CharSequence? = null,
        isPaid: Boolean? = null,
        isDone: Boolean? = null,
        date: Any? = null,
        page: Int,
        count: Int
    ): Page<Invoice> = client.get {
        apiUrl(Invoices.schemaName)
        parameters(
            "search" to search,
            "customer" to customer,
            "isPaid" to isPaid,
            "isDone" to isDone,
            "date" to date,
            "page" to page,
            "count" to count
        )
    }

    suspend fun addInvoice(invoice: Invoice): Invoice = client.post {
        apiUrl(Invoices.schemaName)
        jsonBody(invoice)
    }

    suspend fun deleteInvoice(login: Employee, invoice: Invoice): Boolean =
        client.requestStatus(HttpMethod.Delete) {
            apiUrl(Invoices.schemaName)
            jsonBody(invoice)
            parameters("login" to login.name)
        }

    suspend fun getInvoice(id: Id<String, *>): Invoice = client.get {
        apiUrl("${Invoices.schemaName}/$id")
    }

    suspend fun editInvoice(invoice: Invoice): Boolean = client.requestStatus(HttpMethod.Put) {
        apiUrl("${Invoices.schemaName}/${invoice.id}")
        jsonBody(invoice)
    }

    suspend fun nextInvoice(): Int = client.get {
        apiUrl("${Invoices.schemaName}/next")
    }
}