package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.localization.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.coroutines.onAction
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
abstract class DefaultPopOver<out T>(resourced: Resourced, titleId: String) : SimplePopOver(resourced, titleId) {

    protected val defaultButton: Button = @Suppress("LeakingThis") ktfx.layouts.button(getString(R.string.ok)) {
        isDefaultButton = true
    }

    init {
        buttonBar.buttons += defaultButton
    }

    abstract fun getResult(): T

    fun showAt(node: Node, onAction: (T) -> Unit) {
        showAt(node)
        defaultButton.onAction {
            onAction(getResult()!!)
            hide()
        }
    }
}