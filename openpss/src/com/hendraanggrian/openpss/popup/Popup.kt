package com.hendraanggrian.openpss.popup

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.Space
import com.hendraanggrian.openpss.control.Toolbar
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox
import ktfx.scene.layout.updatePadding

interface Popup : Context, NodeInvokable {

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

    fun initialize() {
        setActualContent(ktfx.layouts.vbox {
            // material dialog have extra top padding: https://material.io/develop/web/components/dialogs/
            Toolbar().apply {
                leftItems {
                    label(getString(titleId)) {
                        styleClass.addAll(R.style.bold, R.style.display)
                    }
                }
                rightItems {
                    Space(getDouble(R.dimen.padding_large))()
                    borderPane {
                        centerProperty().bindBidirectional(graphicProperty())
                    }
                }
            }() marginTop getDouble(R.dimen.padding_small) marginBottom getDouble(R.dimen.padding_small)
            contentPane = vbox(getDouble(R.dimen.padding_medium)) {
                updatePadding(
                    left = getDouble(R.dimen.padding_large),
                    right = getDouble(R.dimen.padding_large)
                )
            }
            buttonBar {
                updatePadding(
                    top = getDouble(R.dimen.padding_medium),
                    left = getDouble(R.dimen.padding_large),
                    right = getDouble(R.dimen.padding_large),
                    bottom = getDouble(R.dimen.padding_large)
                )
                buttonInvokable = this
                cancelButton = jfxButton(getString(R.string.close)) {
                    styleClass += R.style.flat
                    isCancelButton = true
                    onAction {
                        dismiss()
                    }
                }
            } marginTop getDouble(R.dimen.padding_medium)
        })
        setOnShown {
            focusedNode?.requestFocus()
        }
    }
}