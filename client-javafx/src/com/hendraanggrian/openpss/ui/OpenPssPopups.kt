package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.Toolbar
import com.jfoenix.controls.JFXButton
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.controls.updatePadding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox

interface OpenPssPopup : FxComponent, NodeInvokable {

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
                    borderPane {
                        centerProperty().listener { _, _, value ->
                            if (value != null) {
                                value marginLeft getDouble(R.value.padding_large)
                            }
                        }
                        centerProperty().bindBidirectional(graphicProperty())
                    }
                }
            }() marginTop getDouble(R.value.padding_small) marginBottom getDouble(R.value.padding_small)
            contentPane = vbox(getDouble(R.value.padding_medium)) {
                updatePadding(
                    left = getDouble(R.value.padding_large),
                    right = getDouble(R.value.padding_large)
                )
            }
            buttonBar {
                updatePadding(
                    top = getDouble(R.value.padding_medium),
                    left = getDouble(R.value.padding_large),
                    right = getDouble(R.value.padding_large),
                    bottom = getDouble(R.value.padding_large)
                )
                buttonInvokable = this
                cancelButton = jfxButton(getString(R.string.close)) {
                    styleClass += R.style.flat
                    isCancelButton = true
                    onAction {
                        dismiss()
                    }
                }
            } marginTop getDouble(R.value.padding_medium)
        })
        setOnShown {
            focusedNode?.requestFocus()
        }
    }
}

/** Defines a popup component that expects result to be returned. */
interface ResultablePopup<T> : OpenPssPopup {

    var defaultButton: Button

    /**
     * @return result of the component.
     */
    val nullableResult: T? get() = null

    override fun initialize() {
        super.initialize()
        buttonInvokable.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += R.style.raised
                buttonType = JFXButton.ButtonType.RAISED
            }
        }
    }
}