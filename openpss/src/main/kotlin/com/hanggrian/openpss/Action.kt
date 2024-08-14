package com.hanggrian.openpss

import com.hanggrian.openpss.db.ExtendedSession
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.PasswordField
import kotlinx.nosql.equal
import kotlinx.nosql.notEqual
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import java.util.ResourceBundle

abstract class Action<T>(private val context: Context, private val requireAdmin: Boolean = false) :
    Context by context {
    override val resourceBundle: ResourceBundle = Language.ofServer().toResourcesBundle()

    abstract fun ExtendedSession.handle(): T

    operator fun invoke(block: ExtendedSession.(T) -> Unit) {
        transaction {
            when {
                requireAdmin && !Employees[login].single().isAdmin -> {
                    PermissionDialog(context).show { admin ->
                        when (admin) {
                            null ->
                                stack.jfxSnackbar(
                                    getString(R.string_invalid_password),
                                    OpenPssApp.DURATION_SHORT,
                                )
                            else -> transaction { block(handle()) }
                        }
                    }
                }
                else -> block(handle())
            }
        }
    }

    private class PermissionDialog(context: Context) :
        ResultableDialog<Employee>(context, R.string_permission_required) {
        private var adminCombo: ComboBox<Employee>
        private var passwordField: PasswordField

        init {
            gridPane {
                hgap = getDouble(R.dimen_padding_medium)
                vgap = getDouble(R.dimen_padding_medium)
                label {
                    text = getString(R.string_permission_required)
                }.grid(0, 0 to 2)
                label(getString(R.string_admin)).grid(1, 0)
                adminCombo =
                    jfxComboBox(
                        transaction {
                            Employees {
                                it.isAdmin.equal(true) and
                                    it.name.notEqual(Employee.BACKDOOR.name)
                            }.toObservableList()
                        },
                    ) { promptText = getString(R.string_admin) }
                        .grid(1, 1)
                label(getString(R.string_password)).grid(2, 0)
                passwordField =
                    jfxPasswordField { promptText = getString(R.string_password) }
                        .grid(2, 1)
            }
            defaultButton.disableProperty().bind(
                adminCombo.valueProperty().isNull or
                    passwordField.textProperty().booleanBindingBy { it.isNullOrBlank() },
            )
        }

        override val focusedNode: Node get() = adminCombo

        override val nullableResult: Employee?
            get() {
                val employee = transaction { Employees[adminCombo.value].single() }
                return when (employee.password) {
                    passwordField.text -> employee
                    else -> null
                }
            }
    }
}
