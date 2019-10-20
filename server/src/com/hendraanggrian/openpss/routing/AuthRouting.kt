package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.Routing
import com.hendraanggrian.openpss.Server
import com.hendraanggrian.openpss.getString
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.schema.Employees
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import kotlinx.nosql.equal

object AuthRouting : Routing({
    get("login") {
        val name = call.getString("name")
        val password = call.getString("password")
        val employee = transaction { Employees { this.name.equal(name) }.singleOrNull() }
        when {
            employee == null || employee.password != password -> call.respond(Employee.NOT_FOUND)
            else -> {
                employee.clearPassword()
                call.respond(employee)
                Server.log("'$employee' logged in")
            }
        }
    }
})
