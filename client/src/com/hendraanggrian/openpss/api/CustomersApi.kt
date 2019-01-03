package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Page
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.Customers
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id

interface CustomersApi : Api {

    suspend fun getCustomers(search: CharSequence, page: Int, count: Int): Page<Customer> = client.get {
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

    suspend fun getCustomer(id: StringId<*>): Customer = client.get {
        apiUrl("${Customers.schemaName}/$id")
    }

    suspend fun editCustomer(login: Employee, id: StringId<*>, customer: Customer): Boolean =
        client.requestStatus(HttpMethod.Put) {
            apiUrl("${Customers.schemaName}/$id")
            jsonBody(customer)
            parameters("login" to login.name)
        }

    suspend fun addContact(id: StringId<*>, contact: Customer.Contact): Customer.Contact =
        client.post {
            apiUrl("${Customers.schemaName}/$id/${Customers.Contacts.schemaName}")
            jsonBody(contact)
        }

    suspend fun deleteContact(
        login: Employee,
        id: StringId<*>,
        contact: Customer.Contact
    ): Boolean =
        client.requestStatus(HttpMethod.Delete) {
            apiUrl("${Customers.schemaName}/$id/${Customers.Contacts.schemaName}")
            jsonBody(contact)
            parameters("employee" to login.name)
        }
}