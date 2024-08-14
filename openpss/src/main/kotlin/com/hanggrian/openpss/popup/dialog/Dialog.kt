package com.hanggrian.openpss.popup.dialog

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.popup.Popup
import com.jfoenix.controls.JFXDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ktfx.getValue
import ktfx.layouts.NodeContainer
import ktfx.setValue

open class Dialog(context: Context, override val titleId: String) :
    JFXDialog(),
    Popup,
    Context by context {
    override lateinit var contentPane: VBox
    override lateinit var buttonManager: NodeContainer
    override lateinit var cancelButton: Button

    override val graphicProperty: ObjectProperty<Node> = SimpleObjectProperty()
    var graphic: Node? by graphicProperty

    init {
        initialize()
        dialogContainer = stack
    }

    override fun setActualContent(region: Region) {
        content = region
    }

    override fun setOnShown(onShown: () -> Unit) = setOnDialogOpened { onShown() }

    override fun dismiss() = close()

    override fun show() {
        val openedDialogs = stack.children.filterIsInstance<Dialog>()
        if (openedDialogs.size > MAX_OPENED_DIALOGS) {
            stack.children -= openedDialogs
        }
        super.show()
    }

    private companion object {
        const val MAX_OPENED_DIALOGS = 3
    }
}
