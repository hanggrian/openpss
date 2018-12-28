package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.schema.Customers
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface CustomerApi : Api {

    suspend fun getCustomers(search: String, page: Int, count: Int): Page<Customer> = client.get {
        apiUrl("$Customers")
        parameters(
            "search" to search,
            "page" to page,
            "count" to count
        )
    }

    suspend fun addCustomer(customer: Customer): Customer = client.post {
        apiUrl("$Customers")
        json()
        body = customer
    }

    suspend fun getCustomer(id: Id<String, *>): Customer = client.get {
        apiUrl("$Customers/$id")
    }

    suspend fun editCustomer(login: Employee, id: Id<String, *>, customer: Customer): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("$Customers/$id")
            json()
            body = customer
            parameters("login" to login.name)
        }

    suspend fun addContact(id: Id<String, *>, contact: Customer.Contact): Customer.Contact = client.post {
        apiUrl("$Customers/$id/${Customers.Contacts}")
        json()
        body = contact
    }

    suspend fun deleteContact(login: Employee, id: Id<String, *>, contact: Customer.Contact): Boolean =
        client.requestStatus(HttpMethod.Delete) {
            apiUrl("$Customers/$id/${Customers.Contacts}")
            json()
            body = contact
            parameters("employee" to login.name)
        }
}