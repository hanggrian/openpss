package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema

object EmployeeAccesses : DocumentSchema<EmployeeAccess>("employee_accesses", EmployeeAccess::class) {
    val employeeId = id("employee_id", Employees)
}

data class EmployeeAccess(
    val employeeId: Id<String, Employees>
) : Document<EmployeeAccesses> {

    override lateinit var id: Id<String, EmployeeAccesses>
}