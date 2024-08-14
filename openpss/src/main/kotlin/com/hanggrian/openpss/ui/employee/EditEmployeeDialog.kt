package com.hanggrian.openpss.ui.employee

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.dialog.TableDialog
import com.hanggrian.openpss.util.clean
import com.hanggrian.openpss.util.doneCell
import com.hanggrian.openpss.util.stringCell
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

class EditEmployeeDialog(context: Context) :
    TableDialog<Employee, Employees>(context, R.string_employee, Employees) {
    init {
        getString(R.string_name).invoke { stringCell { name } }
        getString(R.string_admin).invoke { doneCell { isAdmin } }
        table.contextMenu {
            menuItem {
                textProperty().bind(
                    stringBindingOf(table.selectionModel.selectedIndexProperty()) {
                        when {
                            table.selectionModel.isSelected() ->
                                getString(
                                    when {
                                        table.selectionModel.selectedItem.isAdmin ->
                                            R.string_disable_admin_status
                                        else -> R.string_enable_admin_status
                                    },
                                )
                            else -> null
                        }
                    },
                )
                graphicProperty().bind(
                    bindingOf(table.selectionModel.selectedIndexProperty()) {
                        when {
                            table.selectionModel.isSelected() ->
                                ImageView(
                                    when {
                                        table.selectionModel.selectedItem.isAdmin ->
                                            R.image_menu_admin_off
                                        else -> R.image_menu_admin_on
                                    },
                                )
                            else -> null
                        }
                    },
                )
                bindDisable()
                onAction {
                    ToggleAdminEmployeeAction(
                        this@EditEmployeeDialog,
                        table.selectionModel.selectedItem,
                    ).invoke {
                        refresh()
                    }
                }
            }
            separatorMenuItem()
            (getString(R.string_reset_password)) {
                bindDisable()
                onAction {
                    ResetAdminEmployeeAction(
                        this@EditEmployeeDialog,
                        table.selectionModel.selectedItem,
                    ).invoke {
                        stack.jfxSnackbar(
                            getString(
                                R.string_change_password_popup_will_appear_when_is_logged_back_in,
                                login.name,
                            ),
                            OpenPssApp.DURATION_LONG,
                        )
                    }
                }
            }
        }
    }

    override fun refresh() {
        table.items =
            transaction {
                Employees { it.name.notEqual(Employee.BACKDOOR.name) }.toMutableObservableList()
            }
    }

    override fun add() =
        AddEmployeePopover(this, R.string_add_employee, false)
            .show(addButton) { employee ->
                (AddEmployeeAction(this, Employee.new(employee!!.clean()))) {
                    table.items.add(it)
                    table.selectionModel.select(it)
                }
            }

    override fun delete() =
        (DeleteEmployeeAction(this, table.selectionModel.selectedItem)) {
            super.delete()
        }

    private fun MenuItem.bindDisable() =
        disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
}
