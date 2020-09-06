package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.dialog.TableDialog
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import kotlinx.nosql.notEqual
import ktfx.bindings.bindingOf
import ktfx.bindings.stringBindingOf
import ktfx.collections.toMutableObservableList
import ktfx.controls.isSelected
import ktfx.coroutines.onAction
import ktfx.jfoenix.controls.jfxSnackbar
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
                textProperty().bind(
                    stringBindingOf(table.selectionModel.selectedIndexProperty()) {
                        when {
                            table.selectionModel.isSelected() -> getString(
                                when {
                                    table.selectionModel.selectedItem.isAdmin -> R.string.disable_admin_status
                                    else -> R.string.enable_admin_status
                                }
                            )
                            else -> null
                        }
                    }
                )
                graphicProperty().bind(
                    bindingOf(table.selectionModel.selectedIndexProperty()) {
                        when {
                            table.selectionModel.isSelected() -> ImageView(
                                when {
                                    table.selectionModel.selectedItem.isAdmin -> R.image.menu_admin_off
                                    else -> R.image.menu_admin_on
                                }
                            )
                            else -> null
                        }
                    }
                )
                bindDisable()
                onAction {
                    (ToggleAdminEmployeeAction(this@EditEmployeeDialog, table.selectionModel.selectedItem)) {
                        refresh()
                    }
                }
            }
            separatorMenuItem()
            (getString(R.string.reset_password)) {
                bindDisable()
                onAction {
                    (ResetAdminEmployeeAction(this@EditEmployeeDialog, table.selectionModel.selectedItem)) {
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

    override fun add() = AddEmployeePopover(this, R.string.add_employee, false).show(addButton) { employee ->
        (AddEmployeeAction(this, Employee.new(employee!!.clean()))) {
            table.items.add(it)
            table.selectionModel.select(it)
        }
    }

    override fun delete() = (DeleteEmployeeAction(this, table.selectionModel.selectedItem)) {
        super.delete()
    }

    private fun MenuItem.bindDisable() = disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
}
