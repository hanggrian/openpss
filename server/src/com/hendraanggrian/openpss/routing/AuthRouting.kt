package com.hendraanggrian.openpss.routing

import com.hendraanggrian.openpss.OpenPSSServer
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.Employees
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import kotlinx.nosql.equal

object AuthRouting : OpenPssRouting({
    get("login") {
        val name = call.getString("name")
        val password = call.getString("password")
        val employee = transaction { Employees { this.name.equal(name) }.singleOrNull() }
        when {
            employee == null || employee.password != password -> call.respond(Employee.NOT_FOUND)
            else -> {
                employee.clearPassword()
                call.respond(employee)
                OpenPSSServer.log("'$employee' logged in")
            }
        }
    }
})