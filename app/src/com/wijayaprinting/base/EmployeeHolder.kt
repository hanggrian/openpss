package com.wijayaprinting.base

import com.wijayaprinting.db.Employee

interface EmployeeHolder {

    /** Field name starts with underscore to avoid conflict with [com.wijayaprinting.controllers.EmployeeController]. */
    var _employee: Employee

    val employeeName: String get() = _employee.name
    val isFullAccess: Boolean get() = _employee.fullAccess
}