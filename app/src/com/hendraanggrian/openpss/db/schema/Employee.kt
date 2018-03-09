package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.string

object Employees : NamedDocumentSchema<Employee>("employee", Employee::class) {
    val password = string("password")
    val fullAccess = boolean("full_access")
}

open class Employee @JvmOverloads constructor(
    override val name: String,
    var password: String = DEFAULT_PASSWORD,
    var fullAccess: Boolean = false
) : NamedDocument<Employees>() {

    override lateinit var id: Id<String, Employees>

    var firstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        firstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    companion object {
        val BACKDOOR = Employee("Test", "Test", false)
        const val DEFAULT_PASSWORD = "1234"
    }
}