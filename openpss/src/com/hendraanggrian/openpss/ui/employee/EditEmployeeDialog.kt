package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.dialog.TableDialog
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.scene.control.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.nosql.notEqual
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.contextMenu

class EditEmployeeDialog(
    context: Context
) : TableDialog<Employee, Employees>(context, R.string.employee, Employees) {

    private companion object {
        // temporary fix
        const val DELAY = 200L
    }

    init {
        getString(R.string.name)<String> {
            stringCell { name }
        }
        getString(R.string.admin)<Boolean> {
            doneCell { isAdmin }
        }
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(DELAY)
            isAdminProperty().let {
                addButton.disableProperty().bind(!it)
                deleteButton.disableProperty().bind(!selectedBinding or !it)
            }
        }

        table.contextMenu {
            (getString(R.string.toggle_admin)) {
                bindDisable()
                onAction {
                    (ToggleAdminEmployeeAction(this@EditEmployeeDialog, selected!!)) {
                        refresh()
                    }
                }
            }
            (getString(R.string.reset_password)) {
                bindDisable()
                onAction {
                    (ResetAdminEmployeeAction(this@EditEmployeeDialog, selected!!)) {
                        root.jfxSnackbar(
                            getString(R.string.change_password_popup_will_appear_when_is_logged_back_in, login.name),
                            App.DURATION_LONG
                        )
                    }
                }
            }
        }
    }

    override fun refresh() {
        table.items = transaction { Employees { it.name.notEqual(Employee.BACKDOOR.name) }.toMutableObservableList() }
    }

    override fun add() = InputUserPopover(this, R.string.add_employee, false).show(addButton) { employee ->
        (AddEmployeeAction(this, Employee.new(employee!!.clean()))) {
            table.items.add(it)
            select(it)
        }
    }

    private fun MenuItem.bindDisable() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(DELAY)
            disableProperty().bind(!selectedBinding or !isAdminProperty())
        }
    }
}