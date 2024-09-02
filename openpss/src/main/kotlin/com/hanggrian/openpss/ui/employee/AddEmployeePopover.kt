@file:Suppress("ktlint:rulebook:if-else-flattening")

package com.hanggrian.openpss.ui.employee

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.popup.popover.InputPopover
import com.hanggrian.openpss.util.clean
import com.hanggrian.openpss.util.isPersonName
import javafx.beans.binding.BooleanBinding
import ktfx.controlsfx.controls.registerPredicateValidator
import org.controlsfx.validation.Severity

class AddEmployeePopover(
    context: Context,
    titleId: String,
    private val restrictiveInput: Boolean = true,
) : InputPopover(context, titleId) {
    init {
        if (restrictiveInput) {
            editor.registerPredicateValidator<String>(
                getString(R.string__restrictive_input),
                Severity.WARNING,
                false,
            ) { _ ->
                editor.text
                    .split(' ')
                    .none { s -> s.firstOrNull().let { it == null || it.isLowerCase() } }
            }
        }
    }

    override val defaultDisableBinding: BooleanBinding
        get() =
            when {
                restrictiveInput -> !editor.textProperty().isPersonName()
                else -> super.defaultDisableBinding
            }

    override val nullableResult: String get() = editor.text.clean()
}
