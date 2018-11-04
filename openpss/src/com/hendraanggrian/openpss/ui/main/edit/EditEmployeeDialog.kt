package com.hendraanggrian.openpss.ui.main.edit

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.control.dialog.TableDialog
import com.hendraanggrian.openpss.control.popover.InputUserPopover
import javafx.scene.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.nosql.notEqual
import kotlinx.nosql.update
import ktfx.NodeInvokable
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxSnackbar

class EditEmployeeDialog(
    context: Context
) : TableDialog<Employee, Employees>(context, R.string.employee, Employees) {

    private companion object {
        // temporary fix
        const val DELAY = 200L
    }

    override fun NodeInvokable.onCreateActions() {
        jfxButton(getString(R.string.toggle_admin)) {
            bindDisable()
            onAction {
                transaction { Employees[selected!!].projection { admin }.update(!selected!!.admin) }
                refresh()
            }
        }
        jfxButton(getString(R.string.reset_password)) {
            bindDisable()
            onAction {
                transaction { Employees[selected!!].projection { password }.update(Employee.DEFAULT_PASSWORD) }
                root.jfxSnackbar(
                    getString(R.string.change_password_popup_will_appear_when_is_logged_back_in, login.name),
                    App.DURATION_LONG
                )
            }
        }
    }

    init {
        getString(R.string.name)<String> {
            stringCell { name }
        }
        getString(R.string.admin)<Boolean> {
            doneCell { admin }
        }
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(DELAY)
            isAdminProperty().let {
                addButton.disableProperty().bind(!it)
                deleteButton.disableProperty().bind(!selectedBinding or !it)
            }
        }
    }

    override fun refresh() {
        table.items = transaction { Employees { it.name.notEqual(Employee.BACKDOOR.name) }.toMutableObservableList() }
    }

    override fun add() = InputUserPopover(this, R.string.add_employee, false).show(addButton) {
        val employee = Employee.new(it!!)
        employee.id = transaction { Employees.insert(employee) }
        table.items.add(employee)
        select(employee)
    }

    private fun Node.bindDisable() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(DELAY)
            disableProperty().bind(!selectedBinding or !isAdminProperty())
        }
    }
}