package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.getColor
import com.jfoenix.controls.JFXDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import ktfx.NodeManager
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.vbox
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize

open class Dialog(
    context: Context,
    titleId: String
) : JFXDialog(), Context by context, NodeManager {

    override val collection: MutableCollection<Node> get() = contentPane.children

    private lateinit var contentPane: VBox
    protected lateinit var buttonManager: NodeManager
    protected lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node by graphicProperty

    init {
        @Suppress("LeakingThis")
        dialogContainer = root
        content = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            borderPane {
                left = ktfx.layouts.label(getString(titleId)) {
                    fontSize = 18.0
                    textFill = getColor(R.color.accent)
                } align Pos.CENTER_LEFT
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align Pos.CENTER_RIGHT }
                }
            }
            contentPane = vbox(R.dimen.padding_medium.toDouble())
            buttonBar {
                buttonManager = this
                cancelButton = jfxButton(getString(R.string.close)) {
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