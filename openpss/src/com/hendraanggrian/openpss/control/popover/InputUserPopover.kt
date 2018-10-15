package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isName
import javafx.beans.binding.BooleanBinding
import ktfx.NodeManager
import ktfx.controlsfx.registerPredicateValidator
import org.controlsfx.validation.Severity

class InputUserPopover(
    resourced: Resourced,
    titleId: String,
    private val restrictiveInput: Boolean = true
) : InputPopover(resourced, titleId) {

    override fun onCreate(manager: NodeManager) {
        super.onCreate(manager)
        if (restrictiveInput) {
            editor.registerPredicateValidator<String>(
                getString(R.string.name_doesnt_start_with_uppercase_letter_add_anyway),
                Severity.WARNING,
                false
            ) { _ ->
                editor.text.split(' ').none { s -> s.firstOrNull().let { it == null || it.isLowerCase() } }
            }
        }
    }

    override val defaultDisableBinding: BooleanBinding
        get() = when {
            restrictiveInput -> !editor.textProperty().isName()
            else -> super.defaultDisableBinding
        }

    override val nullableResult: String? get() = editor.text.clean()
}