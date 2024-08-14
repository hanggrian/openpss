package com.hanggrian.openpss.popup.popover

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.popup.Popup
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.getValue
import ktfx.layouts.NodeContainer
import ktfx.setValue
import org.controlsfx.control.PopOver

/** Base popup class used across applications. */
open class Popover(context: Context, override val titleId: String) :
    PopOver(),
    Popup,
    Context by context {
    override lateinit var contentPane: VBox
    override lateinit var buttonManager: NodeContainer
    override lateinit var cancelButton: Button

    override val graphicProperty: ObjectProperty<Node> = SimpleObjectProperty()
    var graphic: Node? by graphicProperty

    init {
        initialize()
    }

    override fun setActualContent(region: Region) {
        contentNode = region
    }

    override fun setOnShown(onShown: () -> Unit) = super.setOnShown { onShown() }

    override fun dismiss() = hide()
}
