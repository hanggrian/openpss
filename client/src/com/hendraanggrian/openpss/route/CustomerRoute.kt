package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod

interface CustomerRoute : Route {

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

    suspend fun editCustomer(login: Employee, customer: String, address: String?, note: String?): Boolean =
        client.requestStatus {
            apiUrl("customers/$customer")
            method = HttpMethod.Put
            parameters(
                "address" to address,
                "note" to note,
                "employee" to login.name
            )
        }

    suspend fun addContact(name: String, contact: Customer.Contact): Customer.Contact = client.post {
        apiUrl("customers/$name/contacts")
        body = contact
    }

    suspend fun deleteContact(login: Employee, name: String, contact: Customer.Contact): Boolean =
        client.requestStatus {
            apiUrl("customers/$name/contacts")
            method = HttpMethod.Delete
            body = contact
            parameters("employee" to login.name)
        }
}