package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.lifecycle.Lifecylce
import com.hendraanggrian.openpss.util.getColor
import com.jfoenix.controls.JFXPopup
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize
import org.controlsfx.control.PopOver

/** Base [PopOver] class used across applications. */
@Suppress("LeakingThis")
open class MaterialPopover(
    resourced: Resourced,
    titleId: String
) : JFXPopup(), Lifecylce, Resourced by resourced {

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node by graphicProperty

    init {
        popupContent = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            borderPane {
                left = ktfx.layouts.label(getString(titleId)) {
                    fontSize = 18.0
                    textFill = getColor(R.color.blue)
                } align CENTER_LEFT
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align CENTER_RIGHT }
                }
            }
            onCreate()
            buttonBar {
                jfxButton(getString(R.string.close)) {
                    styleClass += App.STYLE_BUTTON_FLAT
                    isCancelButton = true
                    onAction { hide() }
                }
                onCreateActions()
            } marginTop R.dimen.padding_medium.toDouble()
        }
    }

    fun showAt(node: Node) {
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