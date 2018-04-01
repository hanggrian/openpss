package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.beans.binding.neq
import ktfx.beans.binding.or
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
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.change_password)
        headerText = getString(R.string.change_password)
        graphic = ImageView(R.image.ic_password)

        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.password)) col 0 row 0
            changePasswordField = passwordField { promptText = getString(R.string.password) } col 1 row 0
            label(getString(R.string.change_password)) col 0 row 1
            confirmPasswordField = passwordField { promptText = getString(R.string.change_password) } col 1 row 1
        }
        cancelButton()
        okButton {
            disableProperty().bind(changePasswordField.textProperty().isEmpty
                or confirmPasswordField.textProperty().isEmpty
                or (changePasswordField.textProperty() neq confirmPasswordField.textProperty()))
        }
        setResultConverter { if (it == OK) changePasswordField.text else null }
        later { changePasswordField.requestFocus() }
    }
}