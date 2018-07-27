package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getColor
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.util.Duration.ZERO
import javafxx.beans.value.getValue
import javafxx.beans.value.setValue
import javafxx.coroutines.listener
import javafxx.coroutines.onAction
import javafxx.coroutines.onCloseRequest
import javafxx.layouts.LayoutManager
import javafxx.layouts._ButtonBar
import javafxx.layouts.borderPane
import javafxx.layouts.separator
import javafxx.scene.layout.updatePadding
import org.controlsfx.control.PopOver

/** Base [PopOver] class used across applications. */
abstract class Popover(
    resourced: Resourced,
    titleId: String
) : PopOver(), LayoutManager<Node>, Resourced by resourced {

    private val contentPane: Pane = Pane()
    protected val buttonBar: _ButtonBar = _ButtonBar(null)
    protected val cancelButton: Button = javafxx.layouts.button(getString(R.string.close)) {
        isCancelButton = true
        onAction { hide() }
    }

    override val childs: MutableList<Node> get() = contentPane.children

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node by graphicProperty

    init {
        contentNode = javafxx.layouts.vbox(12.0) {
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
            separator()
            contentPane()
            buttonBar() marginTop 8.0
        }
        buttonBar.buttons += cancelButton
    }

    fun showAt(node: Node) {
        node.scene.window.onCloseRequest { hide(ZERO) }
        val selectedIndex = (node as? TableView<*>)?.selectionModel?.selectedIndex
            ?: (node as? ListView<*>)?.selectionModel?.selectedIndex
            ?: -1
        when (selectedIndex) {
            -1 -> show(node)
            else -> node.localToScreen(node.boundsInLocal).let {
                show(node.scene.window,
                    it.minX + it.width,
                    it.minY + selectedIndex * 22.0 + (0 until selectedIndex).sumByDouble { 2.0 })
            }
        }
    }

    final override fun getString(id: String): String = super.getString(id)
}