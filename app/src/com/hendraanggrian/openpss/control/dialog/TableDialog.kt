package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.StretchableButton
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.application.later
import ktfx.beans.property.toProperty
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.onAction
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts._HBox
import ktfx.layouts.anchorPane
import ktfx.layouts.hbox
import ktfx.layouts.separator
import ktfx.layouts.tableView
import ktfx.scene.control.closeButton

abstract class TableDialog<D : Document<S>, S : DocumentSchema<D>>(
    protected val schema: S,
    resourced: Resourced,
    employee: Employee,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<Nothing>(resourced, headerId, graphicId), TableColumnsBuilder<D>, Selectable<D>, Refreshable {

    protected lateinit var refreshButton: StretchableButton
    protected lateinit var addButton: StretchableButton
    protected lateinit var deleteButton: StretchableButton

    protected lateinit var table: TableView<D>

    override val selectionModel: SelectionModel<D> get() = table.selectionModel

    val buttonBox: _HBox get() = graphic as _HBox

    init {
        isResizable = true
        graphic = hbox(8.0) {
            refreshButton = styledStretchableButton(STYLE_DEFAULT_BUTTON, getString(R.string.refresh),
                ImageView(R.image.btn_refresh_dark)) {
                onAction { refresh() }
            }
            separator(VERTICAL)
            addButton = stretchableButton(getString(R.string.add), ImageView(R.image.btn_add_light)) {
                onAction { this@TableDialog.add() }
            }
            deleteButton = stretchableButton(getString(R.string.delete), ImageView(R.image.btn_delete_light)) {
                onAction { delete() }
                later {
                    transaction {
                        disableProperty().bind(selectedProperty.isNull or !employee.isAdmin().toProperty())
                    }
                }
            }
        }
        anchorPane {
            table = tableView<D> {
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                isEditable = true
            } anchorAll 1.0
        }
        @Suppress("LeakingThis") refresh()
        closeButton()
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