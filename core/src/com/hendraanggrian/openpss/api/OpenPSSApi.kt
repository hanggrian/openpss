package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Log
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class OpenPSSApi : Api() {

    suspend fun login(name: String, password: String) = client.get<Employee>(port = 8080, path = "login") {
        parameter("name", name)
        parameter("password", password)
    }

    suspend fun getCustomers(search: String, page: Int, count: Int) =
        client.get<Page<Customer>>(port = 8080, path = "customers") {
            parameter("search", search)
            parameter("page", page)
            parameter("count", count)
        }

    suspend fun getLogs(page: Int, count: Int) =
        client.get<Page<Log>>(port = 8080, path = "logs") {
            parameter("page", page)
            parameter("count", count)
        }
}