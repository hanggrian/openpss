package com.wijayaprinting.db

import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees : DocumentSchema<Employee>("employee", Employee::class), NamedColumn<Employees> {
    override val name = string("name")
    val password = string("password")
    val fullAccess = boolean("full_access")
}

open class Employee(
        override val name: String,
        var password: String,
        var fullAccess: Boolean
) : Named, Ided<Employees> {
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