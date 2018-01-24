package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Named
import com.wijayaprinting.db.schema.Employees
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import kotfx.asMutableProperty
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