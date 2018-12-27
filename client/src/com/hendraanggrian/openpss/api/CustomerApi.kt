package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Page
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface CustomerApi : Api {

    suspend fun getCustomers(search: String, page: Int, count: Int): Page<Customer> = client.get {
        apiUrl("customers")
        parameters(
            "search" to search,
            "page" to page,
            "count" to count
        )
    }

    suspend fun addCustomer(customer: Customer): Customer = client.post {
        apiUrl("customers")
        json()
        body = customer
    }

    suspend fun getCustomer(id: Id<String, *>): Customer = client.get {
        apiUrl("customers/$id")
    }

    suspend fun editCustomer(login: Employee, id: Id<String, *>, customer: Customer): Boolean = client.requestStatus {
        apiUrl("customers/$id")
        json()
        method = HttpMethod.Put
        body = customer
        parameters("login" to login.name)
    }

    suspend fun addContact(id: Id<String, *>, contact: Customer.Contact): Customer.Contact = client.post {
        apiUrl("customers/$id/contacts")
        json()
        body = contact
    }

    suspend fun deleteContact(login: Employee, id: Id<String, *>, contact: Customer.Contact): Boolean =
        client.requestStatus {
            apiUrl("customers/$id/contacts")
            json()
            method = HttpMethod.Delete
            body = contact
            parameters("employee" to login.name)
        }
}