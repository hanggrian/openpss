package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.binding.BooleanBinding
import javafx.scene.control.TextField
import ktfx.beans.value.isBlank
import ktfx.jfoenix.jfxTextField

open class InputPopover(resourced: Resourced, titleId: String) : ResultablePopover<String>(resourced, titleId) {

    protected val editor: TextField = jfxTextField()

    open val defaultDisableBinding: BooleanBinding get() = editor.textProperty().isBlank()

    init {
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultDisableBinding)
        }
    }

    override val nullableResult: String? get() = editor.text
}