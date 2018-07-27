package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.ResultableDialog
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.control.PasswordField
import javafxx.application.later
import javafxx.beans.value.isBlank
import javafxx.beans.value.neq
import javafxx.beans.value.or
import javafxx.layouts.gridPane
import javafxx.layouts.label
import javafxx.layouts.passwordField
import javafxx.scene.control.cancelButton
import javafxx.scene.control.okButton
import javafxx.scene.layout.gap

class ChangePasswordDialog(resourced: Resourced) : ResultableDialog<String>(
    resourced,
    R.string.change_password,
    R.image.header_change_password
) {

    private lateinit var changePasswordField: PasswordField
    private lateinit var confirmPasswordField: PasswordField

    init {
        gridPane {
            gap = R.dimen.padding_small.toDouble()
            label {
                text = getString(R.string.new_employee_must_assign_new_password_this_will_only_occur_once)
            } col 0 row 0 colSpans 2
            label(getString(R.string.password)) col 0 row 1
            changePasswordField = passwordField { promptText = getString(R.string.password) } col 1 row 1
            label(getString(R.string.confirm_password)) col 0 row 2
            confirmPasswordField = passwordField { promptText = getString(R.string.confirm_password) } col 1 row 2
        }
        cancelButton()
        okButton().disableProperty().bind(changePasswordField.textProperty().isBlank()
            or confirmPasswordField.textProperty().isBlank()
            or changePasswordField.textProperty().neq(confirmPasswordField.textProperty()))
        later { changePasswordField.requestFocus() }
    }

    override val optionalResult: String? get() = changePasswordField.text
}