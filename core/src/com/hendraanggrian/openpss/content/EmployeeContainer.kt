package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction

interface EmployeeContainer {

    val login: Employee

    fun isAdmin(): Boolean = transaction { Employees[login].single().isAdmin }
}