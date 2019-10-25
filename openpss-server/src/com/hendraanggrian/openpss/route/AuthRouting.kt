package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.Server
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.schema.Employees
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.nosql.equal

fun Routing.auth() {
    get("login") {
        val name = call.getString("name")
        val password = call.getString("password")
        val employee = transaction { Employees { this.name.equal(name) }.singleOrNull() }
        when {
            employee == null -> {
                call.respond(Employee.NOT_FOUND)
                Server.log?.error("Employee not found: $name")
            }
            employee.password != password -> {
                call.respond(Employee.NOT_FOUND)
                Server.log?.error("Wrong password: $name")
            }
            else -> {
                employee.clearPassword()
                call.respond(employee)
                Server.log?.info("Logged in: $name")
            }
        }
    }
}
