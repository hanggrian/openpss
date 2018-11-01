package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.space
import com.jfoenix.controls.JFXPopup
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import ktfx.NodeManager
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.vbox
import ktfx.scene.layout.paddingAll

/** Base popup class used across applications. */
open class Popover(
    context: Context,
    titleId: String
) : JFXPopup(), Context by context, NodeManager {

    override val collection: MutableCollection<Node> get() = contentPane.children

    private lateinit var contentPane: VBox
    protected lateinit var buttonManager: NodeManager
    protected lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node? by graphicProperty

    init {
        popupContent = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            borderPane {
                left = ktfx.layouts.label(getString(titleId)) {
                    styleClass += App.STYLE_LABEL_DISPLAY
                } align Pos.CENTER_LEFT
                centerProperty().bind(bindingOf(graphicProperty) {
                    graphic?.let { space(R.dimen.padding_large.toDouble()) }
                })
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align CENTER_RIGHT }
                }
            }
            contentPane = vbox(R.dimen.padding_medium.toDouble())
            buttonBar {
                buttonManager = this
                cancelButton = jfxButton(getString(R.string.close)) {
                    styleClass += App.STYLE_BUTTON_FLAT
                    isCancelButton = true
                    onAction {
                        hide()
                    }
                }
            } marginTop R.dimen.padding_medium.toDouble()
        }
    }
}