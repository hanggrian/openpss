package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.PasswordField
import kotlinx.nosql.equal
import kotlinx.nosql.notEqual
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.jfoenix.jfxComboBox
import ktfx.jfoenix.jfxPasswordField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.layout.gap

class PermissionDialog(component: FxComponent) :
    ResultableDialog<Employee>(component, R.string.permission_required) {

    private lateinit var adminCombo: ComboBox<Employee>
    private lateinit var passwordField: PasswordField

    override val focusedNode: Node? get() = adminCombo

    init {
        gridPane {
            gap = getDouble(R.dimen.padding_medium)
            label {
                text = getString(R.string._permission_required)
            } col 0 row 0 colSpans 2
            label(getString(R.string.admin)) col 0 row 1
            adminCombo = jfxComboBox(transaction {
                Employees.buildQuery {
                    and(it.isAdmin.equal(true))
                    and(it.name.notEqual(Employee.BACKDOOR.name))
                }.toObservableList()
            }) {
                promptText = getString(R.string.admin)
            } col 1 row 1
            label(getString(R.string.password)) col 0 row 2
            passwordField = jfxPasswordField {
                promptText = getString(R.string.password)
            } col 1 row 2
        }
        defaultButton.disableProperty().bind(
            adminCombo.valueProperty().isNull or passwordField.textProperty().isBlank()
        )
    }

    override val nullableResult: Employee?
        get() {
            val employee = transaction { Employees[adminCombo.value].single() }
            return when {
                employee.password == passwordField.text -> employee
                else -> null
            }
        }
}