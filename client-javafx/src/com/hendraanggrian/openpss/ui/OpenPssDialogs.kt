package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.jfxButton
import ktfx.later
import ktfx.layouts.NodeInvokable
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts.anchorPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.tableView
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.setValue
import ktfx.windows.setMinSize
import kotlin.coroutines.CoroutineContext

@Suppress("LeakingThis")
open class OpenPssDialog(
    component: FxComponent,
    override val titleId: String
) : JFXDialog(), OpenPssPopup, FxComponent by component {

    private companion object {
        const val MAX_OPENED_DIALOGS = 3
    }

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

    init {
        initialize()
        dialogContainer = rootLayout
    }

    override fun show() {
        val openedDialogs = rootLayout.children.filterIsInstance<OpenPssDialog>()
        if (openedDialogs.size > MAX_OPENED_DIALOGS) {
            rootLayout.children -= openedDialogs
        }
        super.show()
    }
}

open class ResultableDialog<T>(
    component: FxComponent,
    titleId: String
) : OpenPssDialog(component, titleId), ResultablePopup<T> {

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
) : OpenPssDialog(component, titleId) {

    init {
        ktfx.layouts.label {
            isWrapText = true
            text = content
        }
    }
}

class ConfirmDialog(
    component: FxComponent,
    textId: String? = null
) : ResultableDialog<Unit>(component, R.string.are_you_sure) {

    init {
        textId?.let { label(getString(it)) }
        cancelButton.text = getString(R.string.no)
        defaultButton.text = getString(R.string.yes)
    }
}

@Suppress("LeakingThis")
abstract class TableDialog<D : Document<*>>(
    component: FxComponent,
    titleId: String,
    requestPermissionWhenDelete: Boolean = false
) : OpenPssDialog(component, titleId), TableColumnsBuilder<D>, Refreshable {

    protected lateinit var refreshButton: Button
    protected lateinit var addButton: Button
    protected lateinit var deleteButton: Button
    protected lateinit var table: TableView<D>

    override val focusedNode: Node? get() = table

    init {
        graphic = vbox(getDouble(R.value.padding_medium)) {
            alignment = Pos.CENTER_RIGHT
            hbox(getDouble(R.value.padding_medium)) {
                alignment = Pos.CENTER_RIGHT
                refreshButton = jfxButton(graphic = ImageView(R.image.act_refresh)) {
                    tooltip(getString(R.string.refresh))
                    onAction { refresh() }
                }
                addButton = jfxButton(graphic = ImageView(R.image.act_add)) {
                    tooltip(getString(R.string.add))
                    onAction { add() }
                }
                deleteButton = jfxButton(graphic = ImageView(R.image.act_delete)) {
                    tooltip(getString(R.string.delete))
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
                    later {
                        disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
                    }
                }
            }
        }
        anchorPane {
            table = tableView<D> {
                prefHeight = 275.0
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                isEditable = true
            } anchorAll 1
        }
        refresh()
        later {
            (scene.window as Stage).setMinSize(width, height)
        }
    }

    override fun <T> column(
        text: String?,
        init: (TableColumn<D, T>.() -> Unit)?
    ): TableColumn<D, T> = TableColumn<D, T>(text).also {
        init?.invoke(it)
        table.columns += it
    }

    override fun refresh() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            table.items = refresh().toMutableObservableList()
        }
    }

    abstract suspend fun CoroutineScope.refresh(): List<D>

    abstract fun add()

    abstract suspend fun CoroutineScope.delete(selected: D): Boolean
}