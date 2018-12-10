package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.db.schemas.Employee
import io.ktor.client.request.get

interface AuthRoute : Route {

    suspend fun login(name: String, password: String): Employee = client.get {
        apiUrl("login")
        parameters(
            "name" to name,
            "password" to password
        )
    }

    suspend fun isAdmin(name: String): Boolean = client.get<Employee> {
        apiUrl("employees/$name")
    }.isAdmin
}