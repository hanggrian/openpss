package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.db.schemas.Employee
import io.ktor.client.request.get

interface AuthApi : Api {

    suspend fun login(name: String, password: String): Employee = client.get {
        apiUrl("login")
        parameters(
            "name" to name,
            "password" to password
        )
    }

    suspend fun isAdmin(login: Employee): Boolean = client.get<Employee> {
        apiUrl("employees/${login.id}")
    }.isAdmin
}