package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.StretchableButton
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.control.yesNoAlert
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import javafx.stage.Stage
import javafxx.application.later
import javafxx.beans.property.toProperty
import javafxx.beans.value.or
import javafxx.collections.toMutableObservableList
import javafxx.coroutines.onAction
import javafxx.layouts.LayoutManager
import javafxx.layouts.TableColumnsBuilder
import javafxx.layouts._HBox
import javafxx.layouts.anchorPane
import javafxx.layouts.hbox
import javafxx.layouts.tableView
import javafxx.scene.control.closeButton
import javafxx.stage.setMinSize
import kotlinx.nosql.mongodb.DocumentSchema

@Suppress("LeakingThis")
abstract class TableDialog<D : Document<S>, S : DocumentSchema<D>>(
    protected val schema: S,
    resourced: Resourced,
    employee: Employee,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<Nothing>(resourced, headerId, graphicId), TableColumnsBuilder<D>, Selectable<D>, Refreshable {

    private companion object {
        const val STRETCH_POINT = 400.0
    }

    protected lateinit var refreshButton: StretchableButton
    protected lateinit var addButton: StretchableButton
    protected lateinit var deleteButton: StretchableButton
    protected val extraButtons: _HBox = _HBox(R.dimen.padding_small.toDouble()).apply { alignment = CENTER_RIGHT }

    protected lateinit var table: TableView<D>

    override val selectionModel: SelectionModel<D> get() = table.selectionModel

    init {
        isResizable = true
        anchorPane {
            table = tableView<D> {
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                isEditable = true
            } anchorAll 1.0
        }
        graphic = javafxx.layouts.vbox(R.dimen.padding_small.toDouble()) {
            alignment = CENTER_RIGHT
            hbox(R.dimen.padding_small.toDouble()) {
                alignment = CENTER_RIGHT
                refreshButton = styledStretchableButton(
                    STYLE_DEFAULT_BUTTON,
                    STRETCH_POINT,
                    getString(R.string.refresh),
                    ImageView(R.image.btn_refresh_dark)
                ) {
                    onAction { refresh() }
                }
                space()
                addButton =
                    stretchableButton(STRETCH_POINT, getString(R.string.add), ImageView(R.image.btn_add_light)) {
                        onAction { add() }
                    }
                deleteButton =
                    stretchableButton(STRETCH_POINT, getString(R.string.delete), ImageView(R.image.btn_delete_light)) {
                        onAction { delete() }
                        later {
                            transaction {
                                disableProperty().bind(selectedProperty.isNull or !employee.isAdmin().toProperty())
                            }
                        }
                    }
            }
            extraButtons.onCreateActions()
            if (extraButtons.children.isNotEmpty()) extraButtons()
        }
        refresh()
        closeButton()
        later {
            (dialogPane.scene.window as Stage).setMinSize(width, height)
        }
    }

    /** Override this function to add extra actions. */
    open fun LayoutManager<Node>.onCreateActions() {
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

    private fun delete() = yesNoAlert {
        transaction { schema -= selected!! }
        table.items.remove(selected!!)
    }
}