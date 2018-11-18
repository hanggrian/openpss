package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.base.PopupBase
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.layouts.NodeInvokable
import org.controlsfx.control.PopOver

/** Base popup class used across applications. */
@Suppress("LeakingThis")
open class Popover(
    context: Context,
    override val titleId: String
) : PopOver(), PopupBase, Context by context {

    override fun setActualContent(region: Region) {
        contentNode = region
    }

    override fun setOnShown(onShown: () -> Unit) = super.setOnShown { onShown() }

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