package com.hanggrian.openpss.ui.main

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.PasswordField
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.neq
import ktfx.bindings.or
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.layouts.gridPane
import ktfx.layouts.label

class ChangePasswordDialog(context: Context) :
    ResultableDialog<String>(context, R.string_change_password) {
    private var changePasswordField: PasswordField
    private var confirmPasswordField: PasswordField

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            label { text = getString(R.string__change_password) }
                .grid(0, 0 to 2)
            label(getString(R.string_password))
                .grid(1, 0)
            changePasswordField =
                jfxPasswordField { promptText = getString(R.string_password) }
                    .grid(1, 1)
            label(getString(R.string_confirm_password))
                .grid(2, 0)
            confirmPasswordField =
                jfxPasswordField { promptText = getString(R.string_confirm_password) }
                    .grid(2, 1)
        }
        defaultButton.disableProperty().bind(
            changePasswordField.textProperty().booleanBindingBy { it.isNullOrBlank() }
                or confirmPasswordField.textProperty().booleanBindingBy { it.isNullOrBlank() }
                or changePasswordField.textProperty().neq(confirmPasswordField.textProperty()),
        )
    }

    override val focusedNode: Node get() = changePasswordField

    override val nullableResult: String? get() = changePasswordField.text
}
