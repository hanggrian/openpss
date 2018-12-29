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
        apiUrl(Customers.schemaName)
        parameters(
            "search" to search,
            "page" to page,
            "count" to count
        )
    }

    suspend fun addCustomer(customer: Customer): Customer = client.post {
        apiUrl(Customers.schemaName)
        jsonBody(customer)
    }

    suspend fun getCustomer(id: Id<String, *>): Customer = client.get {
        apiUrl(Customers.schemaName)
    }

    suspend fun editCustomer(login: Employee, id: Id<String, *>, customer: Customer): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("${Customers.schemaName}/$id")
            jsonBody(customer)
            parameters("login" to login.name)
        }

    suspend fun addContact(id: Id<String, *>, contact: Customer.Contact): Customer.Contact = client.post {
        apiUrl("${Customers.schemaName}/$id/${Customers.Contacts.schemaName}")
        jsonBody(contact)
    }

    suspend fun deleteContact(login: Employee, id: Id<String, *>, contact: Customer.Contact): Boolean =
        client.requestStatus(HttpMethod.Delete) {
            apiUrl("${Customers.schemaName}/$id/${Customers.Contacts.schemaName}")
            jsonBody(contact)
            parameters("employee" to login.name)
        }
}