package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Employee
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class OpenPSSApi : Api2() {

    suspend fun login(name: String, password: String) = client.get<Employee>(port = 8080, path = "login") {
        parameter("name", name)
        parameter("password", password)
    }
}