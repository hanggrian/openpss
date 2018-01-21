package com.wijayaprinting.base

import com.wijayaprinting.db.Employee

interface EmployeeContainer {

    var employee: Employee

    val employeeName: String get() = employee.name
    val isFullAccess: Boolean get() = employee.fullAccess
}