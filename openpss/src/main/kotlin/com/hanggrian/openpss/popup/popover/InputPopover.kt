package com.hanggrian.openpss.popup.popover

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import javafx.beans.binding.BooleanBinding
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.bindings.bindingOf
import ktfx.bindings.booleanBindingBy
import ktfx.jfoenix.layouts.jfxTextField

open class InputPopover(context: Context, titleId: String) :
    ResultablePopover<String>(context, titleId) {
    protected val editor: TextField = jfxTextField()

    init {
        defaultButton.run {
            text = getString(R.string_add)
            disableProperty().bind(defaultDisableBinding)
            editor
                .onActionProperty()
                .bind(bindingOf(disableProperty()) { if (isDisable) null else onAction })
        }
    }

    open val defaultDisableBinding: BooleanBinding
        get() = editor.textProperty().booleanBindingBy { it.isNullOrBlank() }

    override val focusedNode: Node? get() = editor

    override val nullableResult: String? get() = editor.text
}
