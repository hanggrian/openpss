package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.Resultable
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import javafxx.coroutines.onAction
import javafxx.layouts.LayoutManager
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
abstract class ResultablePopover<T>(
    resourced: Resourced,
    titleId: String
) : Popover(resourced, titleId), Resultable<T> {

    protected lateinit var defaultButton: Button

    override fun LayoutManager<Node>.onCreateActions() {
        defaultButton = javafxx.layouts.button(getString(R.string.ok)) {
            isDefaultButton = true
        }()
    }

    fun showAt(node: Node, onAction: (T) -> Unit) {
        showAt(node)
        defaultButton.onAction {
            onAction(nullableResult!!)
            hide()
        }
    }
}