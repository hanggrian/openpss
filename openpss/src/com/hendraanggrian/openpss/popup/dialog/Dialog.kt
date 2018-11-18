package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.Popup
import com.jfoenix.controls.JFXDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.layouts.NodeInvokable
import java.util.WeakHashMap

@Suppress("LeakingThis")
open class Dialog(
    context: Context,
    override val titleId: String
) : JFXDialog(), Popup, Context by context {

    override fun setActualContent(region: Region) {
        content = region
    }

    override fun setOnShown(onShown: () -> Unit) = setOnDialogOpened { onShown() }

    override fun dismiss() = close()

    override lateinit var contentPane: VBox
    override lateinit var buttonInvokable: NodeInvokable
    override lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    override fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node? by graphicProperty

    override val stringConverters: MutableMap<String, StringConverter<Number>> = WeakHashMap()

    init {
        initialize()
        dialogContainer = stack
    }
}