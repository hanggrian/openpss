package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Employee
import kotlinx.nosql.boolean
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees : DocumentSchema<Employee>("employees", Employee::class),
    NamedSchema {
    override val name = string("name")
    val password = string("password")
    val isAdmin = boolean("is_admin")
}