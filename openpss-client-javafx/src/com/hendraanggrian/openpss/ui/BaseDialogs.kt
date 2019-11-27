package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.nosql.Document
import com.jfoenix.controls.JFXDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.collections.toMutableObservableList
import ktfx.controls.constrained
import ktfx.controls.notSelectedBinding
import ktfx.controls.setMinSize
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.anchorPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.tableView
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.setValue

@Suppress("LeakingThis")
open class BaseDialog(
    component: FxComponent,
    override val titleId: String
) : JFXDialog(), BasePopup, FxComponent by component {

    private companion object {
        const val MAX_OPENED_DIALOGS = 3
    }

    override fun setActualContent(region: Region) {
        content = region
    }

    override fun setOnShown(onShown: () -> Unit) = setOnDialogOpened { onShown() }

    override fun dismiss() = close()

    override lateinit var contentPane: VBox
    override lateinit var buttonManager: NodeManager
    override lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    override fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node? by graphicProperty

    init {
        initialize()
        dialogContainer = rootLayout
    }

    override fun show() {
        val openedDialogs = rootLayout.children.filterIsInstance<BaseDialog>()
        if (openedDialogs.size > MAX_OPENED_DIALOGS) {
            rootLayout.children -= openedDialogs
        }
        super.show()
    }
}

open class ResultableDialog<T>(
    component: FxComponent,
    titleId: String
) : BaseDialog(component, titleId), ResultablePopup<T> {

    override lateinit var defaultButton: Button

    fun show(
        context: CoroutineContext = Dispatchers.JavaFx,
        onAction: suspend CoroutineScope.(T?) -> Unit
    ) {
        super.show()
        defaultButton.onAction(context) {
            onAction(nullableResult)
            close()
        }
    }
}

class TextDialog(
    component: FxComponent,
    titleId: String,
    content: String = ""
) : BaseDialog(component, titleId) {

    init {
        label {
            isWrapText = true
            text = content
        }
    }
}

class ConfirmDialog(
    component: FxComponent,
    textId: String? = null
) : ResultableDialog<Unit>(component, R2.string.are_you_sure) {

    init {
        textId?.let { label(getString(it)) }
        cancelButton.text = getString(R2.string.no)
        defaultButton.text = getString(R2.string.yes)
    }
}

@Suppress("LeakingThis")
abstract class TableDialog<D : Document<*>>(
    component: FxComponent,
    titleId: String,
    requestPermissionWhenDelete: Boolean = false
) : BaseDialog(component, titleId), Refreshable {

    protected val refreshButton: Button
    protected val addButton: Button
    protected val deleteButton: Button
    protected lateinit var table: TableView<D>

    override val focusedNode: Node? get() = table

    init {
        graphic = vbox(getDouble(R.value.padding_medium)) {
            alignment = Pos.CENTER_RIGHT
            hbox(getDouble(R.value.padding_medium)) {
                alignment = Pos.CENTER_RIGHT
                refreshButton = jfxButton(graphic = ImageView(R.image.action_refresh)) {
                    tooltip(getString(R2.string.refresh))
                    onAction { refresh() }
                }
                addButton = jfxButton(graphic = ImageView(R.image.action_add)) {
                    tooltip(getString(R2.string.add))
                    onAction { this@TableDialog.add() }
                }
                deleteButton = jfxButton(graphic = ImageView(R.image.action_delete)) {
                    tooltip(getString(R2.string.delete))
                    onAction {
                        val action = {
                            ConfirmDialog(this@TableDialog).show {
                                val selected = table.selectionModel.selectedItem
                                if (delete(selected)) {
                                    table.items.remove(selected)
                                }
                            }
                        }
                        when {
                            requestPermissionWhenDelete -> withPermission { action() }
                            else -> action()
                        }
                    }
                    runLater {
                        disableProperty().bind(table.selectionModel.notSelectedBinding)
                    }
                }
            }
        }
        anchorPane {
            table = tableView<D> {
                prefHeight = 275.0
                constrained()
                isEditable = true
            } anchorAll 1.0
        }
        refresh()
        runLater {
            (scene.window as Stage).setMinSize(width, height)
        }
    }

    fun <T> column(text: String? = null): TableColumn<D, T> =
        TableColumn<D, T>(text).also { table.columns += it }

    inline fun <T> column(text: String? = null, init: TableColumn<D, T>.() -> Unit): TableColumn<D, T> =
        column<T>(text).apply(init)

    inline operator fun <T> String.invoke(init: TableColumn<D, T>.() -> Unit): TableColumn<D, T> =
        column(this, init)

    override fun refresh() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            table.items = refresh().toMutableObservableList()
        }
    }

    abstract suspend fun CoroutineScope.refresh(): List<D>

    abstract fun add()

    abstract suspend fun CoroutineScope.delete(selected: D): Boolean
}
