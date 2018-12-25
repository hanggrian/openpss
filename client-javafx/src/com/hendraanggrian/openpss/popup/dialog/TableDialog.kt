package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.data.Document
import com.hendraanggrian.openpss.ui.Refreshable
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.later
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts.anchorPane
import ktfx.layouts.hbox
import ktfx.layouts.tableView
import ktfx.layouts.tooltip
import ktfx.windows.setMinSize

@Suppress("LeakingThis")
abstract class TableDialog<D : Document<*>>(
    component: FxComponent,
    titleId: String,
    requestPermissionWhenDelete: Boolean = false
) : Dialog(component, titleId), TableColumnsBuilder<D>, Refreshable {

    protected lateinit var refreshButton: Button
    protected lateinit var addButton: Button
    protected lateinit var deleteButton: Button
    protected lateinit var table: TableView<D>

    override val focusedNode: Node? get() = table

    init {
        graphic = ktfx.layouts.vbox(getDouble(R.dimen.padding_medium)) {
            alignment = CENTER_RIGHT
            hbox(getDouble(R.dimen.padding_medium)) {
                alignment = CENTER_RIGHT
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
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                isEditable = true
            } anchorAll 1.0
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