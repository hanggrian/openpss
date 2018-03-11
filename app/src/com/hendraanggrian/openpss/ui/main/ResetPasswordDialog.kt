package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kfx.application.later
import kfx.beans.binding.neq
import kfx.beans.binding.or
import kfx.layouts.gridPane
import kfx.layouts.label
import kfx.layouts.passwordField
import kfx.scene.control.cancelButton
import kfx.scene.control.icon
import kfx.scene.control.okButton
import kfx.scene.layout.gaps

class ResetPasswordDialog(resourced: Resourced) : Dialog<String>(), Resourced by resourced {

    lateinit var changePasswordField: PasswordField
    lateinit var confirmPasswordField: PasswordField

    init {
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.change_password)
        headerText = getString(R.string.change_password)
        graphic = ImageView(R.image.ic_key)

        dialogPane.content = gridPane {
            gaps = 8
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