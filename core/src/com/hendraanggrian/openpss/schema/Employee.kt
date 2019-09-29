package com.hendraanggrian.openpss.schema

import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.boolean
import kotlinx.nosql.string

object Employees : NamedDocumentSchema<Employee>("employees", Employee::class) {
    override val name = string("name")
    val password = string("password")
    val isAdmin = boolean("is_admin")
}

data class Employee(
    override var name: String,
    var password: String,
    @SerializedName("is_admin") var isAdmin: Boolean
) : NamedDocument<Employees> {

    companion object {

        val NOT_FOUND: Employee =
            Employee("", "", false)

        const val DEFAULT_PASSWORD = "1234"
        val BACKDOOR: Employee =
            Employee("Test", DEFAULT_PASSWORD, true)

        fun new(name: String): Employee =
            Employee(name, DEFAULT_PASSWORD, false)
    }

    override lateinit var id: StringId<Employees>

    var isFirstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        isFirstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    override fun toString(): String = name
}
