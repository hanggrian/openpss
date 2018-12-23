package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import javafx.beans.binding.BooleanBinding
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.bindings.buildBinding
import ktfx.bindings.isBlank
import ktfx.jfoenix.jfxTextField

open class InputPopover(component: FxComponent, titleId: String) : ResultablePopover<String>(component, titleId) {

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