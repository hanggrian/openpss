package com.hendraanggrian.openpss.db.schemas

import kotlinx.nosql.Id
import kotlinx.nosql.boolean
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDBSession

object EmployeeAccesses : DocumentSchema<EmployeeAccess>("employee_accesses", EmployeeAccess::class) {
    val employeeId = id("employee_id", Employees)
    val access = boolean("access")
}

data class EmployeeAccess(
    val employeeId: Id<String, Employees>,
    val access: Boolean
)

@Suppress("NOTHING_TO_INLINE")
inline fun MongoDBSession.isFullAccess(employee: Employee): Boolean =
    EmployeeAccesses.find { employeeId.equal(employee.id) }.singleOrNull()?.access ?: false