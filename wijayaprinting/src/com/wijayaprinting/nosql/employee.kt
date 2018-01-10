package com.wijayaprinting.nosql

import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees : DocumentSchema<Employee>("employee", Employee::class) {
    val name = string("name")
    val password = string("password")
    val fullAccess = boolean("full_access")
}

data class Employee(
        val name: String,
        val password: String,
        val fullAccess: Boolean
) {
    val id: Id<String, Employees>? = null

    val firstTimeLogin: Boolean get() = password == DEFAULT_PASSWORD

    companion object {
        private const val DEFAULT_PASSWORD = "1234"
    }
}