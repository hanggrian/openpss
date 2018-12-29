package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.PasswordField
import ktfx.bindings.isBlank
import ktfx.bindings.neq
import ktfx.bindings.or
import ktfx.controls.gap
import ktfx.jfoenix.jfxPasswordField
import ktfx.layouts.gridPane
import ktfx.layouts.label

class ChangePasswordDialog(component: FxComponent) : ResultableDialog<String>(component, R.string.change_password) {

    private lateinit var changePasswordField: PasswordField
    private lateinit var confirmPasswordField: PasswordField

    override val focusedNode: Node? get() = changePasswordField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label {
                text = getString(R.string._change_password)
            } col 0 row 0 colSpans 2
            label(getString(R.string.password)) col 0 row 1
            changePasswordField = jfxPasswordField {
                promptText = getString(R.string.password)
            } col 1 row 1
            label(getString(R.string.confirm_password)) col 0 row 2
            confirmPasswordField = jfxPasswordField {
                promptText = getString(R.string.confirm_password)
            } col 1 row 2
        }
        defaultButton.disableProperty().bind(
            changePasswordField.textProperty().isBlank()
                or confirmPasswordField.textProperty().isBlank()
                or changePasswordField.textProperty().neq(confirmPasswordField.textProperty())
        )
    }

    override val nullableResult: String? get() = changePasswordField.text
}