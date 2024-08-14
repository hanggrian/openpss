package com.hanggrian.openpss.popup.dialog

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.ui.Refreshable
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.collections.toMutableObservableList
import ktfx.controls.RIGHT
import ktfx.controls.TableColumnScope
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.anchorPane
import ktfx.layouts.hbox
import ktfx.layouts.tableView
import ktfx.layouts.tooltip
import ktfx.runLater

abstract class TableDialog<D : Document<S>, S : DocumentSchema<D>>(
    context: Context,
    titleId: String,
    protected val schema: S,
) : Dialog(context, titleId),
    TableColumnScope<D>,
    Refreshable {
    protected var refreshButton: Button
    protected var addButton: Button
    protected var deleteButton: Button
    protected lateinit var table: TableView<D>

    init {
        graphic =
            ktfx.layouts.vbox(getDouble(R.dimen_padding_medium)) {
                alignment = RIGHT
                hbox(getDouble(R.dimen_padding_medium)) {
                    alignment = RIGHT
                    refreshButton =
                        jfxButton(graphic = ImageView(R.image_act_refresh)) {
                            tooltip(getString(R.string_refresh))
                            onAction { refresh() }
                        }
                    addButton =
                        jfxButton(graphic = ImageView(R.image_act_add)) {
                            tooltip(getString(R.string_add))
                            onAction { add() }
                        }
                    deleteButton =
                        jfxButton(graphic = ImageView(R.image_act_delete)) {
                            tooltip(getString(R.string_delete))
                            onAction { delete() }
                            runLater {
                                disableProperty()
                                    .bind(table.selectionModel.selectedItemProperty().isNull)
                            }
                        }
                }
            }
        anchorPane {
            table =
                tableView<D> {
                    prefHeight = 275.0
                    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                    isEditable = true
                }.anchor(1.0)
        }
        refresh()
        runLater {
            (scene.window as Stage).let {
                it.width = width
                it.height = height
            }
        }
    }

    override val focusedNode: Node? get() = table

    override val columns: MutableCollection<TableColumn<D, *>> get() = table.columns

    override fun refresh() {
        table.items = transaction { schema().toMutableObservableList() }
    }

    abstract fun add()

    open fun delete() =
        ConfirmDialog(this).show {
            transaction { schema -= table.selectionModel.selectedItem }
            table.items.remove(table.selectionModel.selectedItem)
        }
}
