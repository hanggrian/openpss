package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Log
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class OpenPSSApi : Api("http://localhost:8080") {

    suspend fun login(name: String, password: String) = client.get<Employee> {
        apiUrl("login")
        parameter("name", name)
        parameter("password", password)
    }

    suspend fun getCustomers(search: String, page: Int, count: Int) = client.get<Page<Customer>> {
        apiUrl("customers")
        parameter("search", search)
        parameter("page", page)
        parameter("count", count)
    }

    suspend fun addCustomer(customer: Customer) = client.post<Customer> {
        apiUrl("customer")
        json()
        body = customer
    }

    suspend fun getLogs(page: Int, count: Int) = client.get<Page<Log>> {
        apiUrl("logs")
        parameter("page", page)
        parameter("count", count)
    }
}