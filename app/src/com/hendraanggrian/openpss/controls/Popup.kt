package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getColor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.text.Font
import ktfx.application.later
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutManager
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.separator
import ktfx.layouts.vbox
import ktfx.scene.layout.paddingBottom
import ktfx.scene.layout.paddingLeft
import ktfx.scene.layout.paddingRight
import ktfx.scene.layout.paddingTop
import org.controlsfx.control.PopOver

abstract class Popup<out T>(resourced: Resourced, titleId: String) : PopOver(), Resourced by resourced {

    abstract val content: Node

    abstract fun LayoutManager<Node>.buttons()

    open fun getResult(): T? = null

    protected lateinit var buttonBar: ButtonBar

    init {
        contentNode = vbox(12.0) {
            paddingLeft = 16.0
            paddingRight = 16.0
            paddingTop = 12.0
            paddingBottom = 12.0
            label(getString(titleId)) {
                font = Font.font(18.0)
                textFill = getColor(R.color.teal)
            }
            separator()
            // For popup with prefill
            later {
                content.add()
                buttonBar = buttonBar {
                    buttons()
                }
            }
        }
    }

    fun show(node: Node, onAction: (T) -> Unit) {
        show(node)
        // Since content is later
        buttonBar.buttons.map { it as Button }.single { it.isDefaultButton }.onAction {
            onAction(getResult()!!)
            hide()
        }
    }
}