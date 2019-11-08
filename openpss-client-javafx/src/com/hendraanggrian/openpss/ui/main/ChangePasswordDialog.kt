package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.ui.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.PasswordField
import ktfx.bindings.isBlank
import ktfx.bindings.neq
import ktfx.bindings.or
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label

class ChangePasswordDialog(component: FxComponent) :
    ResultableDialog<String>(component, R2.string.change_password) {

    private val changePasswordField: PasswordField
    private val confirmPasswordField: PasswordField

    override val focusedNode: Node? get() = changePasswordField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label {
                gridAt(0, 0)
                colSpans = 2
                text = getString(R2.string._change_password)
            }
            label(getString(R2.string.password)) {
                gridAt(1, 0)
            }
            changePasswordField = jfxPasswordField {
                gridAt(1, 1)
                promptText = getString(R2.string.password)
            }
            label(getString(R2.string.confirm_password)) {
                gridAt(2, 0)
            }
            confirmPasswordField = jfxPasswordField {
                gridAt(2, 1)
                promptText = getString(R2.string.confirm_password)
            }
        }
        defaultButton.disableProperty().bind(
            changePasswordField.textProperty().isBlank()
                or confirmPasswordField.textProperty().isBlank()
                or changePasswordField.textProperty().neq(confirmPasswordField.textProperty())
        )
    }

    override val nullableResult: String? get() = changePasswordField.text
}
