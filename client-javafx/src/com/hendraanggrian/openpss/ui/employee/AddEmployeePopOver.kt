package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.ui.InputPopOver
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isPersonName
import javafx.beans.binding.BooleanBinding
import ktfx.controlsfx.registerPredicateValidator
import org.controlsfx.validation.Severity

class AddEmployeePopOver(
    component: FxComponent,
    titleId: String,
    private val restrictiveInput: Boolean = true
) : InputPopOver(component, titleId) {

    init {
        if (restrictiveInput) {
            editor.registerPredicateValidator<String>(
                getString(R2.string.name_doesnt_start_with_uppercase_letter_add_anyway),
                Severity.WARNING,
                false
            ) {
                editor.text.split(' ')
                    .none { s -> s.firstOrNull().let { it == null || it.isLowerCase() } }
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