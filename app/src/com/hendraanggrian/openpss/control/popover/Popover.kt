package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.ActionManager
import com.hendraanggrian.openpss.control.dialog.Dialog
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getColor
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafxx.beans.value.getValue
import javafxx.beans.value.setValue
import javafxx.coroutines.listener
import javafxx.coroutines.onAction
import javafxx.layouts.LayoutManager
import javafxx.layouts.borderPane
import javafxx.layouts.button
import javafxx.layouts.buttonBar
import javafxx.scene.layout.updatePadding
import org.controlsfx.control.PopOver

/** Base [PopOver] class used across applications. */
@Suppress("LeakingThis")
open class Popover(
    private val resourced: Resourced,
    titleId: String
) : PopOver(), LayoutManager<Node>, ActionManager, Resourced by resourced {

    private val contentPane = Pane()

    override val childs get() = contentPane.children!!

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node by graphicProperty

    init {
        contentNode = javafxx.layouts.vbox(R.dimen.padding_small.toDouble()) {
            updatePadding(12.0, 16.0, 12.0, 16.0)
            borderPane {
                left = javafxx.layouts.label(getString(titleId)) {
                    font = Font.font(18.0)
                    textFill = getColor(R.color.blue)
                } align CENTER_LEFT
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align CENTER_RIGHT }
                }
            }
            contentPane()
            buttonBar {
                button(getString(R.string.close)) {
                    isCancelButton = true
                    onAction { hide() }
                }
                onCreateActions()
            } marginTop R.dimen.padding_small.toDouble()
        }
    }

    fun showAt(node: Node) {
        // to avoid error when closing window/stage during popover display
        if (resourced is Dialog<*>) {
            resourced.dialogPane.scene.window.setOnCloseRequest {
                isAnimated = false
                hide()
            }
        }
        // now check for coordinate to show popover
        val selectedIndex = (node as? TableView<*>)?.selectionModel?.selectedIndex
            ?: (node as? ListView<*>)?.selectionModel?.selectedIndex
        when (selectedIndex) {
            null -> show(node)
            else -> {
                val bounds = node.localToScreen(node.boundsInLocal)
                show(node.scene.window,
                    bounds.minX + bounds.width,
                    bounds.minY + selectedIndex * 22.0 + (0 until selectedIndex).sumByDouble { 2.0 })
            }
        }
    }

    final override fun getString(id: String): String = super.getString(id)
}