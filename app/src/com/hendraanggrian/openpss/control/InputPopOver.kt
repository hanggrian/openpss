package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.localization.Resourced
import javafx.beans.binding.BooleanBinding
import javafx.scene.control.TextField
import ktfx.beans.value.isBlank
import ktfx.layouts.textField

open class InputPopOver(resourced: Resourced, titleId: String) : DefaultPopOver<String>(resourced, titleId) {

    protected val editor: TextField = textField()

    open val defaultDisableBinding: BooleanBinding = editor.textProperty().isBlank()

    init {
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultDisableBinding)
        }
    }

    override fun getResult(): String = editor.text
}