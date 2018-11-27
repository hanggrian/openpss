package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.PasswordField
import ktfx.beans.value.isBlank
import ktfx.beans.value.neq
import ktfx.beans.value.or
import ktfx.jfoenix.jfxPasswordField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.layout.gap

class ChangePasswordDialog(context: Context) : ResultableDialog<String>(context, R.string.change_password) {

    private lateinit var changePasswordField: PasswordField
    private lateinit var confirmPasswordField: PasswordField

    override val focusedNode: Node? get() = changePasswordField

    init {
        gridPane {
            gap = getDouble(R.dimen.padding_medium)
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