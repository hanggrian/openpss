package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.server.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import kotlinx.nosql.equal

object AuthRoute : Route({
    "login" {
        get {
            val name = call.getString("name")
            val password = call.getString("password")
            val employee = transaction { Employees { it.name.equal(name) }.singleOrNull() }
            when {
                employee == null -> call.respond(HttpStatusCode.NotFound)
                employee.password != password -> call.respond(HttpStatusCode.Unauthorized)
                else -> {
                    employee.clearPassword()
                    call.respond(employee)
                }
            }
        }
    }
})