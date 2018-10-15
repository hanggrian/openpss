package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.binding.BooleanBinding
import javafx.scene.control.TextField
import ktfx.NodeManager
import ktfx.beans.value.isBlank
import ktfx.layouts.textField

open class InputPopover(resourced: Resourced, titleId: String) : ResultablePopover<String>(resourced, titleId) {

    protected lateinit var editor: TextField

    open val defaultDisableBinding: BooleanBinding get() = editor.textProperty().isBlank()

    override fun onCreate(manager: NodeManager) {
        super.onCreate(manager)
        manager.run {
            editor = textField()
        }
    }

    override fun onCreateActions(manager: NodeManager) {
        super.onCreateActions(manager)
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultDisableBinding)
        }
    }

    override val nullableResult: String? get() = editor.text
}