package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.base.PopupBase
import com.jfoenix.controls.JFXPopup
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.NodeInvokable
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue

/** Base popup class used across applications. */
@Suppress("LeakingThis")
open class Popover(
    context: Context,
    override val titleId: String
) : JFXPopup(), PopupBase, Context by context {

    override fun setActualContent(region: Region) {
        popupContent = region
    }

    override fun dismiss() = hide()

    override lateinit var contentPane: VBox
    override lateinit var buttonInvokable: NodeInvokable
    override lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    override fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node? by graphicProperty

    init {
        initialize()
    }
}