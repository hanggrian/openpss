package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.PasswordField
import ktfx.bindings.asBoolean
import ktfx.bindings.neq
import ktfx.bindings.or
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.layouts.gridPane
import ktfx.layouts.label

class ChangePasswordDialog(context: Context) : ResultableDialog<String>(context, R.string.change_password) {

    private var changePasswordField: PasswordField
    private var confirmPasswordField: PasswordField

    override val focusedNode: Node? get() = changePasswordField

    init {
        gridPane {
            hgap = getDouble(R.dimen.padding_medium)
            vgap = getDouble(R.dimen.padding_medium)
            label {
                text = getString(R.string._change_password)
            }.grid(0, 0 to 2)
            label(getString(R.string.password)).grid(1, 0)
            changePasswordField = jfxPasswordField {
                promptText = getString(R.string.password)
            }.grid(1, 1)
            label(getString(R.string.confirm_password)).grid(2, 0)
            confirmPasswordField = jfxPasswordField {
                promptText = getString(R.string.confirm_password)
            }.grid(2, 1)
        }
        defaultButton.disableProperty().bind(
            changePasswordField.textProperty().asBoolean { it.isNullOrBlank() }
                or confirmPasswordField.textProperty().asBoolean { it.isNullOrBlank() }
                or changePasswordField.textProperty().neq(confirmPasswordField.textProperty())
        )
    }

    override val nullableResult: String? get() = changePasswordField.text
}
