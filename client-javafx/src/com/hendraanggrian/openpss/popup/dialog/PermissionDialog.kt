package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.Employee
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.PasswordField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
            GlobalScope.launch(Dispatchers.JavaFx) {
                adminCombo = jfxComboBox(api.getEmployees()
                    .filter { it.isAdmin && it.name != Employee.BACKDOOR.name }
                    .toObservableList()
                ) {
                    promptText = getString(R.string.admin)
                } col 1 row 1
            }
            label(getString(R.string.password)) col 0 row 2
            passwordField = jfxPasswordField {
                promptText = getString(R.string.password)
            } col 1 row 2
        }
        defaultButton.disableProperty().bind(
            adminCombo.valueProperty().isNull or passwordField.textProperty().isBlank()
        )
    }

    override val nullableResult: Employee? get() = runBlocking { api.login(adminCombo.value.name, passwordField.text) }
}