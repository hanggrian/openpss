package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.TableDialog
import com.hendraanggrian.openpss.control.popover.InputUserPopover
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.stringCell
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.FX
import ktfx.scene.control.choiceBoxCellFactory

class EditEmployeeDialog(
    resourced: Resourced,
    employee: Employee
) : TableDialog<Employee, Employees>(Employees, resourced, employee, R.string.employee, R.image.header_employee) {

    init {
        getString(R.string.name)<String> {
            stringCell { name }
        }
        getString(R.string.role)<Employee.Role> {
            choiceBoxCellFactory(Employee.Role.values().toObservableList())
        }
        launch(FX) {
            delay(100)
            transaction { employee.isAtLeast(Employee.Role.EXECUTIVE) }.toReadOnlyProperty().let {
                addButton.disableProperty().bind(!it)
                // editButton.disableProperty().bind(!selectedBinding or !it)
                deleteButton.disableProperty().bind(!selectedBinding or !it)
            }
        }
    }

    override fun add() = InputUserPopover(this, R.string.add_employee, false).showAt(addButton) {
        val employee = Employee.new(it)
        employee.id = transaction { Employees.insert(employee) }
        table.items.add(employee)
        select(employee)
    }
}