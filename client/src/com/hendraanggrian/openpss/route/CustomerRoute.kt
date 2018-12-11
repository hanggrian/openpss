package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.content.Page
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

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

    suspend fun getCustomer(id: Id<String, *>): Customer = client.get {
        apiUrl("customers/$id")
    }

    suspend fun editCustomer(login: Employee, id: Id<String, *>, address: String?, note: String?): Boolean =
        client.requestStatus {
            apiUrl("customers/$id")
            method = HttpMethod.Put
            parameters(
                "address" to address,
                "note" to note,
                "employee" to login.name
            )
        }

    suspend fun addContact(id: Id<String, *>, contact: Customer.Contact): Customer.Contact = client.post {
        apiUrl("customers/$id/contacts")
        body = contact
    }

    suspend fun deleteContact(login: Employee, id: Id<String, *>, contact: Customer.Contact): Boolean =
        client.requestStatus {
            apiUrl("customers/$id/contacts")
            method = HttpMethod.Delete
            body = contact
            parameters("employee" to login.name)
        }
}