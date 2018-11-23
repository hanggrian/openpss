package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import javafx.beans.binding.BooleanBinding
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.binding.buildBinding
import ktfx.beans.value.isBlank
import ktfx.jfoenix.jfxTextField

open class InputPopover(context: Context, titleId: String) : ResultablePopover<String>(context, titleId) {

    protected val editor: TextField = jfxTextField()

    open val defaultDisableBinding: BooleanBinding get() = editor.textProperty().isBlank()

    override val focusedNode: Node? get() = editor

    init {
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultDisableBinding)
            editor.onActionProperty().bind(buildBinding(disableProperty()) { if (isDisable) null else onAction })
        }
    }

    override val nullableResult: String? get() = editor.text
}