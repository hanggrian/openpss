package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.Employees
import kotlinx.nosql.Id
import java.io.Serializable

data class Employee(
    override var name: String,
    var password: String,
    var isAdmin: Boolean
) : Document<Employees>, Named, Serializable {

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