package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.popup.Popup
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.getValue
import ktfx.layouts.NodeInvokable
import ktfx.setValue
import org.controlsfx.control.PopOver

/** Base popup class used across applications. */
@Suppress("LeakingThis")
open class Popover(
    component: FxComponent,
    override val titleId: String
) : PopOver(), Popup, FxComponent by component {

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