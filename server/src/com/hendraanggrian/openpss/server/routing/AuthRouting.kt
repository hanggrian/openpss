package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.nosql.equal

fun Routing.routeAuth() {
    get("/login") {
        var employee: Employee? = null
        transaction {
            // check first time installation
            Database.TABLES.mapNotNull { it as? Setupable }.forEach { it.setup(this) }
            // check login credentials
            employee = Employees { it.name.equal(call.parameters["name"]) }.singleOrNull()
        }
        when {
            employee == null -> call.respond(HttpStatusCode.NotFound)
            employee!!.password != call.parameters["password"] -> call.respond(HttpStatusCode.Unauthorized)
            else -> {
                employee!!.clearPassword()
                call.respond(employee!!)
            }
        }
    }
}