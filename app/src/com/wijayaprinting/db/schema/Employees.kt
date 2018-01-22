package com.wijayaprinting.db.schema

import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.NamedDocumentSchema
import kotlinx.nosql.boolean
import kotlinx.nosql.string

object Employees : NamedDocumentSchema<Employee>("employee", Employee::class) {
    val password = string("password")
    val fullAccess = boolean("full_access")
}