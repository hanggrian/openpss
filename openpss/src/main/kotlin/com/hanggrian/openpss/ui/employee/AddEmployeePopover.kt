package com.hanggrian.openpss.ui.employee

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.popup.popover.InputPopover
import com.hanggrian.openpss.util.clean
import com.hanggrian.openpss.util.isPersonName
import javafx.beans.binding.BooleanBinding
import ktfx.controlsfx.controls.registerPredicateValidator
import org.controlsfx.validation.Severity

class AddEmployeePopover : InputPopover {
    private val restrictiveInput: Boolean

    constructor(context: Context, titleId: String, restrictiveInput: Boolean = true) :
        super(context, titleId) {
        this.restrictiveInput = restrictiveInput
        if (!restrictiveInput) {
            return
        }
        editor.registerPredicateValidator<String>(
            getString(R.string_name_doesnt_start_with_uppercase_letter_add_anyway),
            Severity.WARNING,
            false,
        ) { _ ->
            editor.text
                .split(' ')
                .none { s -> s.firstOrNull().let { it == null || it.isLowerCase() } }
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
