package com.hendraanggrian.openpss.server.controller

import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import kotlinx.nosql.equal

interface AuthController {

    fun login(name: String, password: String): Employee {
        lateinit var employee: Employee
        transaction {
            // check first time installation
            Database.TABLES.mapNotNull { it as? Setupable }.forEach { it.setup(this) }
            // check login credentials
            employee = checkNotNull(Employees { it.name.equal(name) }.singleOrNull()) { "Employee not found" }
            check(employee.password == password) { "Invalid password" }
        }
        employee.clearPassword()
        return employee
    }
}