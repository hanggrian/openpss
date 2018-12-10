package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.server.util.getString
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.nosql.equal

fun Routing.routeAuth() {
    route("login") {
        get {
            val name = call.getString("name")
            val password = call.getString("password")
            val employee = transaction {
                // check first time installation
                Database.TABLES.mapNotNull { it as? Setupable }.forEach { it.setup(this) }
                // check login credentials
                Employees { it.name.equal(name) }.singleOrNull()
            }
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
}