package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.R
import com.jfoenix.controls.JFXDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import ktfx.NodeInvokable
import ktfx.beans.binding.buildBinding
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.vbox
import ktfx.scene.layout.paddingAll

open class Dialog(
    context: Context,
    titleId: String
) : JFXDialog(), Context by context, NodeInvokable {

    override fun <R : Node> R.invoke(): R = also { contentPane.children += it }

    private lateinit var contentPane: VBox
    protected lateinit var buttonInvokable: NodeInvokable
    protected lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node? by graphicProperty

    init {
        @Suppress("LeakingThis")
        dialogContainer = root
        content = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            borderPane {
                left = ktfx.layouts.label(getString(titleId)) {
                    styleClass += App.STYLE_LABEL_DISPLAY
                } align Pos.CENTER_LEFT
                centerProperty().bind(buildBinding(graphicProperty) {
                    graphic?.let { com.hendraanggrian.openpss.control.space(R.dimen.padding_large.toDouble()) }
                })
                rightProperty().run {
                    bindBidirectional(graphicProperty)
                    listener { _, _, value -> value align Pos.CENTER_RIGHT }
                }
            }
            contentPane = vbox(R.dimen.padding_medium.toDouble())
            buttonBar {
                buttonInvokable = this
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