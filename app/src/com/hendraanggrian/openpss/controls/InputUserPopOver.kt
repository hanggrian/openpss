package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.validator
import javafx.beans.binding.BooleanBinding
import org.controlsfx.validation.Severity.WARNING

class InputUserPopOver(
    resourced: Resourced,
    titleId: String,
    private val restrictiveInput: Boolean = true
) : InputPopOver(resourced, titleId) {

    init {
        if (restrictiveInput) editor.validator<String>(
            getString(R.string.name_doesnt_start_with_uppercase_letter_add_anyway), WARNING, false) {
            editor.text.split(" ").none { it.firstOrNull().let { it == null || it.isLowerCase() } }
        }
    }

    override val defaultDisableBinding: BooleanBinding
        get() = when {
            restrictiveInput -> !editor.textProperty().isName()
            else -> super.defaultDisableBinding
        }

    override fun getResult(): String = editor.text.clean()
}