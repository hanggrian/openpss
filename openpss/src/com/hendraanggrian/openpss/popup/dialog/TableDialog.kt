package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Refreshable
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.application.later
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts.anchorPane
import ktfx.layouts.hbox
import ktfx.layouts.tableView
import ktfx.layouts.tooltip
import ktfx.stage.setMinSize

@Suppress("LeakingThis")
abstract class TableDialog<D : Document<S>, S : DocumentSchema<D>>(
    context: Context,
    titleId: String,
    protected val schema: S
) : Dialog(context, titleId), TableColumnsBuilder<D>, Refreshable {

    protected lateinit var refreshButton: Button
    protected lateinit var addButton: Button
    protected lateinit var deleteButton: Button

    protected lateinit var table: TableView<D>

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
                    onAction { delete() }
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
        table.items = transaction { schema().toMutableObservableList() }
    }

    abstract fun add()

    open fun delete() = ConfirmDialog(this).show {
        transaction { schema -= table.selectionModel.selectedItem }
        table.items.remove(table.selectionModel.selectedItem)
    }
}