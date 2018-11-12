package com.hendraanggrian.openpss.control.base

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import javafx.beans.property.ObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.NodeInvokable
import ktfx.beans.binding.buildBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.vbox
import ktfx.scene.layout.paddingAll

interface PopupBase : BaseControl, Context, NodeInvokable {

    override fun <R : Node> R.invoke(): R = also { contentPane.children += it }

    fun setActualContent(region: Region)
    fun setOnShown(onShown: () -> Unit)
    fun dismiss()

    val focusedNode: Node? get() = null

    val titleId: String

    var contentPane: VBox
    var buttonInvokable: NodeInvokable
    var cancelButton: Button

    fun graphicProperty(): ObjectProperty<Node>

    override fun initialize() {
        setActualContent(ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            borderPane {
                left = ktfx.layouts.label(getString(titleId)) {
                    styleClass.addAll("bold", "display")
                } align Pos.CENTER_LEFT
                centerProperty().bind(buildBinding(graphicProperty()) {
                    graphicProperty().get()?.let {
                        com.hendraanggrian.openpss.control.space(R.dimen.padding_large.toDouble())
                    }
                })
                rightProperty().run {
                    bindBidirectional(graphicProperty())
                    listener { _, _, value -> value align Pos.CENTER_RIGHT }
                }
            }
            contentPane = vbox(R.dimen.padding_medium.toDouble()) marginTop R.dimen.padding_medium.toDouble()
            buttonBar {
                buttonInvokable = this
                cancelButton = jfxButton(getString(R.string.close)) {
                    styleClass += "flat"
                    isCancelButton = true
                    onAction {
                        dismiss()
                    }
                }
            } marginTop R.dimen.padding_medium.toDouble()
        })
        setOnShown {
            focusedNode?.requestFocus()
        }
    }
}