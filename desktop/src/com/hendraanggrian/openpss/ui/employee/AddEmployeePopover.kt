package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.popover.InputPopover
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isPersonName
import javafx.beans.binding.BooleanBinding
import ktfx.controlsfx.registerPredicateValidator
import org.controlsfx.validation.Severity

class AddEmployeePopover(
    context: Context,
    titleId: String,
    private val restrictiveInput: Boolean = true
) : InputPopover(context, titleId) {

    init {
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
            restrictiveInput -> !editor.textProperty().isPersonName()
            else -> super.defaultDisableBinding
        }

    override val nullableResult: String? get() = editor.text.clean()
}