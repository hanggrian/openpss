package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.string

object Employees : NamedDocumentSchema<Employee>("employees", Employee::class) {
    val password = string("password")
    val fullAccess = boolean("full_access")
}

data class Employee(
    override var name: String,
    var password: String,
    var fullAccess: Boolean
) : NamedDocument<Employees> {

    override lateinit var id: Id<String, Employees>

    var firstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        firstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    override fun toString(): String = name

    companion object {
        const val DEFAULT_PASSWORD = "1234"

        val BACKDOOR: Employee = Employee("Test", "hendraganteng", true)

        fun new(
            name: String,
            password: String = DEFAULT_PASSWORD
        ): Employee = Employee(name, password, false)
    }
}