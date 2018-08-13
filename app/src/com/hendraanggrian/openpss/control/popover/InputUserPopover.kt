package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.validator
import javafx.beans.binding.BooleanBinding
import org.controlsfx.validation.Severity.WARNING

class InputUserPopover(
    resourced: Resourced,
    titleId: String,
    private val restrictiveInput: Boolean = true
) : InputPopover(resourced, titleId) {

    init {
        if (restrictiveInput) editor.validator<String>(
            getString(R.string.name_doesnt_start_with_uppercase_letter_add_anyway),
            WARNING,
            false
        ) { _ -> editor.text.split(" ").none { s -> s.firstOrNull().let { it == null || it.isLowerCase() } } }
    }

    override val defaultDisableBinding: BooleanBinding
        get() = when {
            restrictiveInput -> !editor.textProperty().isName()
            else -> super.defaultDisableBinding
        }

    override val optionalResult: String? get() = editor.text.clean()
}