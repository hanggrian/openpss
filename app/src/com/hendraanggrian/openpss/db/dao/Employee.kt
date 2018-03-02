package com.hendraanggrian.openpss.db.dao

import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.schema.Employees
import kotlinx.nosql.Id

open class Employee(
    override val name: String,
    var password: String,
    var fullAccess: Boolean
) : Named<Employees> {
    override lateinit var id: Id<String, Employees>

    var firstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        firstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    override fun toString(): String = name

    companion object : Employee("Test", "Test", false) {
        const val DEFAULT_PASSWORD = "1234"
    }
}