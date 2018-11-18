package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.dialog.TableDialog
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.scene.control.MenuItem
import kotlinx.nosql.notEqual
import ktfx.beans.binding.buildBinding
import ktfx.beans.binding.buildStringBinding
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.contextMenu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem

class EditEmployeeDialog(
    context: Context
) : TableDialog<Employee, Employees>(context, R.string.employee, Employees) {

    init {
        getString(R.string.name)<String> {
            stringCell { name }
        }
        getString(R.string.admin)<Boolean> {
            doneCell { isAdmin }
        }
        table.contextMenu {
            menuItem {
                textProperty().bind(buildStringBinding(selectedProperty) {
                    when {
                        selected != null -> getString(
                            when {
                                selected!!.isAdmin -> R.string.disable_admin_status
                                else -> R.string.enable_admin_status
                            }
                        )
                        else -> null
                    }
                })
                graphicProperty().bind(buildBinding(selectedProperty) {
                    when {
                        selected != null -> ktfx.layouts.imageView(
                            when {
                                selected!!.isAdmin -> R.image.menu_admin_off
                                else -> R.image.menu_admin_on
                            }
                        )
                        else -> null
                    }
                })
                bindDisable()
                onAction {
                    (ToggleAdminEmployeeAction(this@EditEmployeeDialog, selected!!)) {
                        refresh()
                    }
                }
            }
            separatorMenuItem()
            (getString(R.string.reset_password)) {
                bindDisable()
                onAction {
                    (ResetAdminEmployeeAction(this@EditEmployeeDialog, selected!!)) {
                        stack.jfxSnackbar(
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

    override fun delete() = (DeleteEmployeeAction(this, selected!!)) {
        super.delete()
    }

    private fun MenuItem.bindDisable() = disableProperty().bind(!selectedBinding)
}