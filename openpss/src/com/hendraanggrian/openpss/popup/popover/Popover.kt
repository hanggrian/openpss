package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.popup.dialog.Dialog
import com.hendraanggrian.openpss.util.getColor
import com.jfoenix.controls.JFXPopup
import com.jfoenix.skins.JFXPopupSkin
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.layout.Pane
import ktfx.NodeManager
import ktfx.application.later
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.pane
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize
import org.controlsfx.control.PopOver

/** Base [PopOver] class used across applications. */
open class Popover(
    private val resourced: Resourced,
    titleId: String
) : JFXPopup(), Resourced by resourced, NodeManager {

    override val collection: MutableCollection<Node> get() = contentPane.children

    private lateinit var contentPane: Pane
    protected lateinit var buttonManager: NodeManager

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
                } align Pos.CENTER_LEFT
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align CENTER_RIGHT }
                }
            }
            contentPane = pane()
            buttonBar {
                buttonManager = this
                jfxButton(getString(R.string.close)) {
                    styleClass += App.STYLE_BUTTON_FLAT
                    isCancelButton = true
                    onAction {
                        hide()
                    }
                }
            } marginTop R.dimen.padding_medium.toDouble()
        }
    }

    override fun show(node: Node) {
        val selectedIndex = (node as? TableView<*>)?.selectionModel?.selectedIndex
            ?: (node as? ListView<*>)?.selectionModel?.selectedIndex
        when {
            selectedIndex == null || resourced is Dialog -> super.show(node)
            else -> {
                val bounds = node.localToScreen(node.boundsInLocal)
                super.show(
                    node.scene.window,
                    bounds.minX + bounds.width,
                    bounds.minY + selectedIndex * 22.0 + (0 until selectedIndex).sumByDouble { 2.0 }
                )
                (skin as JFXPopupSkin).run {
                    reset(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 0.0, 0.0)
                    later { animate() }
                }
            }
        }
    }

    final override fun getString(id: String): String = super.getString(id)
}