@file:Suppress("ClassName")

package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import kotlinx.nosql.equal

@Location("/login")
class login(val name: String, val password: String)

fun Routing.routeAuth() {
    get<login> { input ->
        val employee = transaction {
            // check first time installation
            Database.TABLES.mapNotNull { it as? Setupable }.forEach { it.setup(this) }
            // check login credentials
            Employees { it.name.equal(input.name) }.singleOrNull()
        }
        when {
            employee == null -> call.respond(HttpStatusCode.NotFound)
            employee.password != input.password -> call.respond(HttpStatusCode.Unauthorized)
            else -> {
                employee.clearPassword()
                call.respond(employee)
            }
        }
    }
}