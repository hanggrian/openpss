package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Employees
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import kotlinx.nosql.equal

object AuthRoute : Routing({
    get("login") {
        val name = call.getString("name")
        val password = call.getString("password")
        val employee =
            transaction { Employees { this.name.equal(name) }.singleOrNull() }
        when {
            employee == null -> call.respond(HttpStatusCode.NotFound)
            employee.password != password -> call.respond(HttpStatusCode.Unauthorized)
            else -> {
                employee.clearPassword()
                call.respond(employee)
            }
        }
    }
})