package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.style
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.beans.value.isBlank
import ktfx.beans.value.neq
import ktfx.beans.value.or
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.passwordField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.icon
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class ResetPasswordDialog(resourced: Resourced) : Dialog<String>(), Resourced by resourced {

    private lateinit var changePasswordField: PasswordField
    private lateinit var confirmPasswordField: PasswordField

    init {
        style()
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.change_password)
        headerText = getString(R.string.change_password)
        graphic = ImageView(R.image.ic_password)
        dialogPane.content = gridPane {
            gap = 8.0
            label(R.string.new_employee_must_assign_new_password_this_will_only_occur_once) col 0 row 0 colSpans 2
            label(getString(R.string.password)) col 0 row 1
            changePasswordField = passwordField { promptText = getString(R.string.password) } col 1 row 1
            label(getString(R.string.confirm_password)) col 0 row 2
            confirmPasswordField = passwordField { promptText = getString(R.string.confirm_password) } col 1 row 2
        }
        cancelButton()
        okButton().disableProperty().bind(changePasswordField.textProperty().isBlank()
            or confirmPasswordField.textProperty().isBlank()
            or changePasswordField.textProperty().neq(confirmPasswordField.textProperty()))
        setResultConverter { if (it == OK) changePasswordField.text else null }
        later { changePasswordField.requestFocus() }
    }
}