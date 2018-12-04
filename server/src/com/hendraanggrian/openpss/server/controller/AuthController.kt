package com.hendraanggrian.openpss.server.controller

import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.server.db.transaction
import kotlinx.nosql.equal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Suppress("unused")
interface AuthController {

    @RequestMapping("/login")
    fun login(
        @RequestParam(value = "name") name: String,
        @RequestParam(value = "password") password: String
    ): Employee {
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