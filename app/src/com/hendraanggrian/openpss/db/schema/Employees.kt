package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.NamedDocumentSchema
import com.hendraanggrian.openpss.db.dao.Employee
import kotlinx.nosql.boolean
import kotlinx.nosql.string

object Employees : NamedDocumentSchema<Employee>("employee", Employee::class) {
    val password = string("password")
    val fullAccess = boolean("full_access")
}