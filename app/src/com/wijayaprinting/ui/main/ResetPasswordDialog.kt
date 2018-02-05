package com.wijayaprinting.ui.main

import com.wijayaprinting.R
import com.wijayaprinting.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.bindings.neq
import kotfx.bindings.or
import kotfx.dialogs.button
import kotfx.dialogs.content
import kotfx.dialogs.icon
import kotfx.gap
import kotfx.runLater
import kotfx.scene.gridPane
import kotfx.scene.label
import kotfx.scene.passwordField

class ResetPasswordDialog(resourced: Resourced) : Dialog<String>(), Resourced by resourced {

    lateinit var changePasswordField: PasswordField
    lateinit var confirmPasswordField: PasswordField

    init {
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.change_password)
        headerText = getString(R.string.change_password)
        graphic = ImageView(R.image.ic_key)

        content = gridPane {
            gap(8)
            label(getString(R.string.password)) col 0 row 0
            changePasswordField = passwordField { promptText = getString(R.string.password) } col 1 row 0
            label(getString(R.string.change_password)) col 0 row 1
            confirmPasswordField = passwordField { promptText = getString(R.string.change_password) } col 1 row 1
        }
        button(CANCEL)
        button(OK).disableProperty().bind(changePasswordField.textProperty().isEmpty
                or confirmPasswordField.textProperty().isEmpty
                or (changePasswordField.textProperty() neq confirmPasswordField.textProperty()))
        setResultConverter { if (it == OK) changePasswordField.text else null }
        runLater { changePasswordField.requestFocus() }
    }
}