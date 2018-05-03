package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.util.enumValueOfId
import com.hendraanggrian.openpss.util.id
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees : DocumentSchema<Employee>("employees", Employee::class), NamedSchema {
    override val name = string("name")
    val password = string("password")
    val role = string("role")
}

data class Employee(
    override var name: String,
    var password: String,
    var role: String
) : Document<Employees>, Named {
    companion object {
        const val DEFAULT_PASSWORD = "1234"
        val BACKDOOR: Employee = Employee("Test", "hendraganteng", Role.EXECUTIVE.id)

        fun new(name: String): Employee = Employee(name, DEFAULT_PASSWORD, Role.SALES.id)
    }

    override lateinit var id: Id<String, Employees>

    var isFirstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        isFirstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    var typedRole: Role
        get() = enumValueOfId(role)
        set(value) {
            role = value.id
        }

    override fun toString(): String = name

    enum class Role(val accessLevel: Int) {
        SALES(1), MANAGER(2), EXECUTIVE(3);

        override fun toString(): String = id.capitalize()
    }
}