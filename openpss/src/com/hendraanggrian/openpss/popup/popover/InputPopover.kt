package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import javafx.beans.binding.BooleanBinding
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.bindings.asBoolean
import ktfx.bindings.bindingOf
import ktfx.jfoenix.layouts.jfxTextField

open class InputPopover(context: Context, titleId: String) : ResultablePopover<String>(context, titleId) {

    protected val editor: TextField = jfxTextField()

    open val defaultDisableBinding: BooleanBinding get() = editor.textProperty().asBoolean { it.isNullOrBlank() }

    override val focusedNode: Node? get() = editor

    init {
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultDisableBinding)
            editor.onActionProperty().bind(bindingOf(disableProperty()) { if (isDisable) null else onAction })
        }
    }

    override val nullableResult: String? get() = editor.text
}
