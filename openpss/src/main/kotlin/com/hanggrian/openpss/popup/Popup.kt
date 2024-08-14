@file:Suppress("ktlint:rulebook:qualifier-consistency")

package com.hanggrian.openpss.popup

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.Toolbar
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
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.styledLabel
import ktfx.layouts.vbox

interface Popup :
    Context,
    NodeContainer {
    val titleId: String

    var contentPane: VBox
    var buttonManager: NodeContainer
    var cancelButton: Button

    val graphicProperty: ObjectProperty<Node>

    val focusedNode: Node? get() = null

    override fun <T : Node> addChild(child: T): T = child.also { contentPane.children += it }

    fun setActualContent(region: Region)

    fun setOnShown(onShown: () -> Unit)

    fun dismiss()

    fun initialize() {
        setActualContent(
            ktfx.layouts.vbox {
                // material dialog have extra top padding
                // https://material.io/develop/web/components/dialogs/
                addChild(
                    Toolbar().apply {
                        leftItems {
                            styledLabel(getString(titleId), null, R.style_bold, R.style_display)
                        }
                        rightItems {
                            borderPane {
                                centerProperty().listener { _, _, value ->
                                    value?.margin(insetsOf(left = getDouble(R.dimen_padding_large)))
                                }
                                centerProperty().bindBidirectional(graphicProperty)
                            }
                        }
                    },
                ).margin(insetsOf(vertical = getDouble(R.dimen_padding_small)))
                contentPane =
                    vbox(getDouble(R.dimen_padding_medium)) {
                        padding =
                            insetsOf(
                                left = getDouble(R.dimen_padding_large),
                                right = getDouble(R.dimen_padding_large),
                            )
                    }
                buttonBar {
                    buttonManager = this
                    padding =
                        insetsOf(
                            top = getDouble(R.dimen_padding_medium),
                            left = getDouble(R.dimen_padding_large),
                            right = getDouble(R.dimen_padding_large),
                            bottom = getDouble(R.dimen_padding_large),
                        )
                    cancelButton =
                        styledJfxButton(getString(R.string_close), null, R.style_flat) {
                            isCancelButton = true
                            onAction {
                                dismiss()
                            }
                        }
                }.margin(insetsOf(top = getDouble(R.dimen_padding_medium)))
            },
        )
        setOnShown {
            focusedNode?.requestFocus()
        }
    }
}
