package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getColor
import com.jfoenix.controls.JFXDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Pane
import ktfx.NodeManager
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

open class Dialog(
    resourced: Resourced,
    titleId: String
) : JFXDialog(), Resourced by resourced, NodeManager {

    override val collection: MutableCollection<Node> get() = contentPane.children

    private lateinit var contentPane: Pane
    protected lateinit var buttonManager: NodeManager

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node by graphicProperty

    init {
        content = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            borderPane {
                left = ktfx.layouts.label(getString(titleId)) {
                    fontSize = 18.0
                    textFill = getColor(R.color.blue)
                } align Pos.CENTER_LEFT
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align Pos.CENTER_RIGHT }
                }
            }
            contentPane = pane()
            buttonBar {
                buttonManager = this
                jfxButton(getString(R.string.close)) {
                    styleClass += App.STYLE_BUTTON_FLAT
                    isCancelButton = true
                    onAction {
                        close()
                    }
                }
            } marginTop R.dimen.padding_medium.toDouble()
        }
    }
}