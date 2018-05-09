package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getColor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.text.Font
import javafx.util.Duration.ZERO
import ktfx.application.later
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
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

    protected lateinit var buttonBar: ButtonBar
    protected lateinit var cancelButton: Button
    protected lateinit var defaultButton: Button

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
                    cancelButton = button(getString(R.string.close)) {
                        isCancelButton = true
                        onAction { hide() }
                    }
                    buttons()
                }
            }
        }
    }

    open fun getResult(): T? = null

    open fun LayoutManager<Node>.buttons() {
    }

    fun showAt(node: Node) {
        show(node)
        later { node.scene.window.setOnCloseRequest { hide(ZERO) } }
    }

    fun showAt(node: Node, onAction: (T) -> Unit) {
        showAt(node)
        // Since content is later
        later {
            buttonBar.buttons.map { it as Button }.single { it.isDefaultButton }.onAction {
                onAction(getResult()!!)
                hide()
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    protected inline fun LayoutManager<Node>.defaultButton(
        textId: String = R.string.ok,
        noinline init: ((@LayoutDsl Button).() -> Unit)? = null
    ): Button = button(getString(textId)) {
        isDefaultButton = true
        if (init != null) init()
    }.also { defaultButton = it }
}