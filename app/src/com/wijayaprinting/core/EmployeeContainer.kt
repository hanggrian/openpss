package com.wijayaprinting.core

import com.wijayaprinting.nosql.Employee

interface EmployeeContainer {

    var employee: Employee

    val employeeName: String get() = employee.name
    val isFullAccess: Boolean get() = employee.fullAccess
}