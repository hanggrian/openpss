package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.validator
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.value.isBlank
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.textField
import org.controlsfx.validation.Severity.WARNING

class UserPopup(
    resourced: Resourced,
    titleId: String,
    private val restrictiveInput: Boolean = true
) : Popup<String>(resourced, titleId) {

    private val nameField: TextField get() = content as TextField

    override val content: Node = textField {
        promptText = getString(R.string.name)
        if (restrictiveInput) validator<String>(
            getString(R.string.name_doesnt_start_with_uppercase_letter_add_anyway), WARNING, false) {
            nameField.text.split(" ").none { it.firstOrNull().let { it == null || it.isLowerCase() } }
        }
    }

    override fun LayoutManager<Node>.buttons() {
        button(getString(R.string.add)) {
            isDefaultButton = true
            disableProperty().bind(when {
                restrictiveInput -> !nameField.textProperty().isName()
                else -> nameField.textProperty().isBlank()
            })
        }
    }

    override fun getResult(): String = when {
        restrictiveInput -> nameField.text.clean()
        else -> nameField.text
    }
}