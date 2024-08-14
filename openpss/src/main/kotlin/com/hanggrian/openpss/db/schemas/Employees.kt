package com.hanggrian.openpss.db.schemas

import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.ExtendedSession
import com.hanggrian.openpss.db.Named
import com.hanggrian.openpss.db.NamedSchema
import com.hanggrian.openpss.db.Setupable
import com.hanggrian.openpss.util.isEmpty
import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees :
    DocumentSchema<Employee>("employees", Employee::class),
    NamedSchema,
    Setupable {
    override val name = string("name")
    val password = string("password")
    val isAdmin = boolean("is_admin")

    override fun setup(wrapper: ExtendedSession) =
        wrapper.run {
            if (Employees { it.name.equal(Employee.BACKDOOR.name) }.isEmpty()) {
                Employees += Employee.BACKDOOR
            }
        }
}

data class Employee(override var name: String, var password: String, var isAdmin: Boolean) :
    Document<Employees>,
    Named {
    override lateinit var id: Id<String, Employees>

    var isFirstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        isFirstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    override fun toString(): String = name

    companion object {
        const val DEFAULT_PASSWORD = "1234"
        val BACKDOOR: Employee = Employee("Test", DEFAULT_PASSWORD, true)

        fun new(name: String): Employee = Employee(name, DEFAULT_PASSWORD, false)
    }
}
