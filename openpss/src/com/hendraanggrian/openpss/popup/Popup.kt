package com.hendraanggrian.openpss.popup

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.Toolbar
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.controls.insetsOf
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.leftItems
import ktfx.jfoenix.layouts.rightItems
import ktfx.jfoenix.layouts.styledJFXButton
import ktfx.layouts.NodeManager
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.styledLabel
import ktfx.layouts.vbox

interface Popup : Context, NodeManager {

    override fun <C : Node> addChild(child: C): C = child.also { contentPane.children += it }

    fun setActualContent(region: Region)
    fun setOnShown(onShown: () -> Unit)
    fun dismiss()

    val focusedNode: Node? get() = null

    val titleId: String

    var contentPane: VBox
    var buttonManager: NodeManager
    var cancelButton: Button

    fun graphicProperty(): ObjectProperty<Node>

    fun initialize() {
        setActualContent(
            ktfx.layouts.vbox {
                // material dialog have extra top padding: https://material.io/develop/web/components/dialogs/
                addChild(
                    Toolbar().apply {
                        leftItems {
                            styledLabel(getString(titleId), null, R.style.bold, R.style.display)
                        }
                        rightItems {
                            borderPane {
                                centerProperty().listener { _, _, value ->
                                    value?.margin(insetsOf(left = getDouble(R.dimen.padding_large)))
                                }
                                centerProperty().bindBidirectional(graphicProperty())
                            }
                        }
                    }
                ).margin(insetsOf(vertical = getDouble(R.dimen.padding_small)))
                contentPane = vbox(getDouble(R.dimen.padding_medium)) {
                    padding = insetsOf(
                        left = getDouble(R.dimen.padding_large),
                        right = getDouble(R.dimen.padding_large)
                    )
                }
                buttonBar {
                    buttonManager = this
                    padding = insetsOf(
                        top = getDouble(R.dimen.padding_medium),
                        left = getDouble(R.dimen.padding_large),
                        right = getDouble(R.dimen.padding_large),
                        bottom = getDouble(R.dimen.padding_large)
                    )
                    cancelButton = styledJFXButton(getString(R.string.close), null, R.style.flat) {
                        isCancelButton = true
                        onAction {
                            dismiss()
                        }
                    }
                }.margin(insetsOf(top = getDouble(R.dimen.padding_medium)))
            }
        )
        setOnShown {
            focusedNode?.requestFocus()
        }
    }
}
