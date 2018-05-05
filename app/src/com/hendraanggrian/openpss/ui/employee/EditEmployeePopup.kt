package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Companion.DEFAULT_PASSWORD
import com.hendraanggrian.openpss.db.schemas.Employee.Role.values
import com.hendraanggrian.openpss.resources.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import ktfx.beans.property.toProperty
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.layout.gap

class EditEmployeePopup(
    resourced: Resourced,
    private val employee: Employee
) : Popup<Employee>(resourced, R.string.edit_employee) {

    private lateinit var nameField: TextField
    private lateinit var roleChoice: ChoiceBox<Employee.Role>
    private lateinit var passwordButton: Button

    private var changed = false.toProperty()

    override val content: Node = gridPane {
        gap = 8.0
        label(getString(R.string.name)) col 0 row 0
        nameField = textField(employee.name) {
            textProperty().listener { changed.set(true) }
        } col 1 row 0
        label(getString(R.string.role)) col 0 row 1
        roleChoice = choiceBox(values().toObservableList()) {
            selectionModel.select(employee.typedRole)
            valueProperty().listener { changed.set(true) }
        } col 1 row 1
        label(getString(R.string.password)) col 0 row 2
        passwordButton = button(getString(R.string.reset_password)) {
            onAction {
                isDisable = true
                changed.set(true)
            }
        } col 1 row 2
    }

    override fun LayoutManager<Node>.buttons() {
        button(getString(R.string.edit)) {
            isDefaultButton = true
            disableProperty().bind(!changed or nameField.textProperty().isBlank())
        }
    }

    override fun getResult(): Employee = employee.apply {
        name = nameField.text
        typedRole = roleChoice.value
        if (passwordButton.isDisable) password = DEFAULT_PASSWORD
    }
}