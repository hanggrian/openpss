package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Companion.DEFAULT_PASSWORD
import com.hendraanggrian.openpss.db.schemas.Employee.Role.EXECUTIVE
import com.hendraanggrian.openpss.db.schemas.Employee.Role.values
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.property.toProperty
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.closeButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class EditEmployeeDialog(resourced: Resourced, employee: Employee) : Dialog<Employee>(), Resourced by resourced {
    private lateinit var nameField: TextField
    private lateinit var roleChoice: ChoiceBox<Employee.Role>
    private lateinit var passwordButton: Button

    private var changed = false.toProperty()

    init {
        headerTitle = getString(R.string.edit_employee)
        graphicIcon = ImageView(R.image.header_employee)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = gridPane {
                gap = 8.0
                label(getString(R.string.name)) col 0 row 0
                nameField = textField(employee.name) {
                    textProperty().listener { changed.set(true) }
                } col 1 row 0
                label(getString(R.string.full_access)) col 0 row 1
                roleChoice = choiceBox(values().toObservableList()) {
                    selectionModel.select(employee.typedRole)
                    valueProperty().listener { changed.set(true) }
                } col 1 row 1
                label(getString(R.string.password)) col 0 row 2
                passwordButton = button(getString(R.string.reset_password)) {
                    onAction {
                        yesNoAlert {
                            isDisable = true
                            changed.set(true)
                        }
                    }
                } col 1 row 2
            }
        }
        closeButton()
        okButton().disableProperty().bind(!transaction { employee.isAtLeast(EXECUTIVE) }.toReadOnlyProperty() or
            !changed or
            nameField.textProperty().isBlank())
        setResultConverter {
            when (it) {
                CLOSE -> null
                else -> employee.apply {
                    name = nameField.text
                    typedRole = roleChoice.value
                    if (passwordButton.isDisable) password = DEFAULT_PASSWORD
                }
            }
        }
    }
}