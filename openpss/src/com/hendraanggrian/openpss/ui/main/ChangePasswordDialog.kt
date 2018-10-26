package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.control.PasswordField
import ktfx.application.later
import ktfx.beans.value.isBlank
import ktfx.beans.value.neq
import ktfx.beans.value.or
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.passwordField
import ktfx.scene.layout.gap

class ChangePasswordDialog(context: Context) : ResultableDialog<String>(context, R.string.change_password) {

    private lateinit var changePasswordField: PasswordField
    private lateinit var confirmPasswordField: PasswordField

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label {
                text = getString(R.string.new_employee_must_assign_new_password_this_will_only_occur_once)
            } col 0 row 0 colSpans 2
            label(getString(R.string.password)) col 0 row 1
            changePasswordField = passwordField {
                promptText = getString(R.string.password)
                later { requestFocus() }
            } col 1 row 1
            label(getString(R.string.confirm_password)) col 0 row 2
            confirmPasswordField = passwordField { promptText = getString(R.string.confirm_password) } col 1 row 2
        }
        defaultButton.disableProperty().bind(
            changePasswordField.textProperty().isBlank()
                or confirmPasswordField.textProperty().isBlank()
                or changePasswordField.textProperty().neq(confirmPasswordField.textProperty())
        )
    }

    override val nullableResult: String? get() = changePasswordField.text
}