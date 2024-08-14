package com.hanggrian.openpss.ui.employee

import com.hanggrian.openpss.Action
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.db.ExtendedSession
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.db.schemas.Employees
import kotlinx.nosql.update

class AddEmployeeAction(context: Context, val employee: Employee) :
    Action<Employee>(context, true) {
    override fun ExtendedSession.handle(): Employee = employee.also { it.id = Employees.insert(it) }
}

class ToggleAdminEmployeeAction(context: Context, val employee: Employee) :
    Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {
        Employees[employee].projection { isAdmin }.update(!employee.isAdmin)
    }
}

class ResetAdminEmployeeAction(context: Context, val employee: Employee) :
    Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {
        Employees[employee].projection { password }.update(Employee.DEFAULT_PASSWORD)
    }
}

class DeleteEmployeeAction(context: Context, val employee: Employee) :
    Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {}
}
