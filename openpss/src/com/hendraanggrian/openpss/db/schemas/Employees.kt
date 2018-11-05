package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.util.isEmpty
import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees : DocumentSchema<Employee>("employees", Employee::class), NamedSchema, Setupable {
    override val name = string("name")
    val password = string("password")
    val admin = boolean("admin")

    override fun setup(wrapper: SessionWrapper) = wrapper.run {
        if (Employees { it.name.equal(Employee.BACKDOOR.name) }.isEmpty()) {
            Employees += Employee.BACKDOOR
        }
    }
}

data class Employee(
    override var name: String,
    var password: String,
    var admin: Boolean
) : Document<Employees>, Named {

    companion object {
        const val DEFAULT_PASSWORD = "1234"
        val BACKDOOR: Employee = Employee("Test", DEFAULT_PASSWORD, true)

        fun new(name: String): Employee = Employee(name, DEFAULT_PASSWORD, false)
    }

    override lateinit var id: Id<String, Employees>

    var isFirstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        isFirstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    override fun toString(): String = name
}