package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Log
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class OpenPSSApi : Api("http://localhost:8080") {

    suspend fun login(name: String, password: String): Employee = client.get {
        apiUrl("login")
        parameter("name", name)
        parameter("password", password)
    }

    suspend fun getCustomers(search: String, page: Int, count: Int): Page<Customer> = client.get {
        apiUrl("customers")
        parameter("search", search)
        parameter("page", page)
        parameter("count", count)
    }

    suspend fun addCustomer(name: String, isCompany: Boolean): Customer = client.post {
        apiUrl("customer")
        json()
        parameter("name", name)
        parameter("isCompany", isCompany)
    }

    suspend fun getLogs(page: Int, count: Int): Page<Log> = client.get {
        apiUrl("logs")
        parameter("page", page)
        parameter("count", count)
    }

    suspend fun getInvoices(
        search: Int,
        customer: String?,
        isPaid: Boolean?,
        date: Any?,
        page: Int,
        count: Int
    ): Page<Invoice> = client.get {
        apiUrl("logs")
        parameter("search", search)
        parameter("customer", customer)
        parameter("isPaid", isPaid)
        parameter("date", date)
        parameter("page", page)
        parameter("count", count)
    }
}