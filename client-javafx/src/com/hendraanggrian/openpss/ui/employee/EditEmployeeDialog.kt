package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.popup.dialog.TableDialog
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.scene.control.MenuItem
import kotlinx.coroutines.CoroutineScope
import ktfx.beans.binding.buildBinding
import ktfx.beans.binding.buildStringBinding
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.contextMenu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.scene.control.isSelected

class EditEmployeeDialog(component: FxComponent) : TableDialog<Employee>(component, R.string.employee, true) {

    init {
        getString(R.string.name)<String> {
            stringCell { name }
        }
        getString(R.string.admin)<Boolean> {
            doneCell { isAdmin }
        }
        table.contextMenu {
            menuItem {
                textProperty().bind(buildStringBinding(table.selectionModel.selectedIndexProperty()) {
                    when {
                        table.selectionModel.isSelected() -> getString(
                            when {
                                table.selectionModel.selectedItem.isAdmin -> R.string.disable_admin_status
                                else -> R.string.enable_admin_status
                            }
                        )
                        else -> null
                    }
                })
                graphicProperty().bind(buildBinding(table.selectionModel.selectedIndexProperty()) {
                    when {
                        table.selectionModel.isSelected() -> ktfx.layouts.imageView(
                            when {
                                table.selectionModel.selectedItem.isAdmin -> R.image.menu_admin_off
                                else -> R.image.menu_admin_on
                            }
                        )
                        else -> null
                    }
                })
                bindDisable()
                onAction {
                    val selected = table.selectionModel.selectedItem
                    App.API.editEmployee(selected.name, selected.password, !selected.isAdmin)
                    refresh()
                }
            }
            separatorMenuItem()
            (getString(R.string.reset_password)) {
                bindDisable()
                onAction {
                    val selected = table.selectionModel.selectedItem
                    App.API.editEmployee(selected.name, Employee.DEFAULT_PASSWORD, selected.isAdmin)
                    rootLayout.jfxSnackbar(
                        getString(R.string.change_password_popup_will_appear_when_is_logged_back_in, login.name),
                        App.DURATION_LONG
                    )
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<Employee> = App.API.getEmployees()

    override fun add() = AddEmployeePopover(this, R.string.add_employee, false).show(addButton) { employee ->
        val added = App.API.addEmployee(employee!!.clean())
        table.items.add(added)
        table.selectionModel.select(added)
    }

    override suspend fun CoroutineScope.delete(selected: Employee): Boolean = App.API.deleteEmployee(selected.name)

    private fun MenuItem.bindDisable() = disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
}