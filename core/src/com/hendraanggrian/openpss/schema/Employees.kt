package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import kotlinx.nosql.boolean
import kotlinx.nosql.string

object Employees : NamedDocumentSchema<Employee>("employees", Employee::class) {
    override val name = string("name")
    val password = string("password")
    val isAdmin = boolean("is_admin")
}