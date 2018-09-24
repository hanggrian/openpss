package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.binding.BooleanBinding
import javafx.scene.control.TextField
import javafxx.beans.value.isBlank
import javafxx.layouts.textField

open class InputPopover(resourced: Resourced, titleId: String) : ResultablePopover<String>(resourced, titleId) {

    protected val editor: TextField = textField()

    open val defaultDisableBinding: BooleanBinding = editor.textProperty().isBlank()

    init {
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultDisableBinding)
        }
    }

    override val nullableResult: String? get() = editor.text
}