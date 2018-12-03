package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import kotlinx.nosql.update

class AddEmployeeAction(component: FxComponent, val employee: Employee) : Action<Employee>(component, true) {

    override val log: String = getString(R.string._log_employee_add, employee.name)

    override fun SessionWrapper.handle(): Employee = employee.also { it.id = Employees.insert(it) }
}

class ToggleAdminEmployeeAction(component: FxComponent, val employee: Employee) : Action<Unit>(component, true) {

    override val log: String = getString(R.string._log_employee_toggle, employee.name, !employee.isAdmin)

    override fun SessionWrapper.handle() {
        Employees[employee].projection { isAdmin }.update(!employee.isAdmin)
    }
}

class ResetAdminEmployeeAction(component: FxComponent, val employee: Employee) : Action<Unit>(component, true) {

    override val log: String = getString(R.string._log_employee_reset, employee.name)

    override fun SessionWrapper.handle() {
        Employees[employee].projection { password }.update(Employee.DEFAULT_PASSWORD)
    }
}

class DeleteEmployeeAction(component: FxComponent, val employee: Employee) : Action<Unit>(component, true) {

    override val log: String = getString(R.string._log_employee_delete, employee.name)

    override fun SessionWrapper.handle() {}
}