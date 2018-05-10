package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutManager
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
abstract class DefaultPopOver<out T>(resourced: Resourced, titleId: String) : SimplePopOver(resourced, titleId) {

    protected lateinit var defaultButton: Button

    override fun buttons(manager: LayoutManager<Node>) = manager.run {
        super.buttons(manager)
        defaultButton = ktfx.layouts.button(getString(R.string.ok)) {
            isDefaultButton = true
        }.add()
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