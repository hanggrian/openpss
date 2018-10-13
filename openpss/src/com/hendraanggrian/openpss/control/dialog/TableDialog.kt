package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.ActionManager
import com.hendraanggrian.openpss.control.StretchableButton
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.yesNoAlert
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import javafx.stage.Stage
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
import ktfx.layouts.tableView
import ktfx.scene.control.closeButton
import ktfx.stage.setMinSize

@Suppress("LeakingThis")
abstract class TableDialog<D : Document<S>, S : DocumentSchema<D>>(
    protected val schema: S,
    resourced: Resourced,
    employee: Employee,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<Nothing>(resourced, headerId, graphicId), ActionManager, TableColumnsBuilder<D>, Selectable<D>, Refreshable {

    private companion object {
        const val STRETCH_POINT = 400.0
    }

    protected lateinit var refreshButton: StretchableButton
    protected lateinit var addButton: StretchableButton
    protected lateinit var deleteButton: StretchableButton
    protected val extraButtons: _HBox = _HBox(R.dimen.padding_medium.toDouble()).apply { alignment = CENTER_RIGHT }

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
        graphic = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            alignment = CENTER_RIGHT
            hbox(R.dimen.padding_medium.toDouble()) {
                alignment = CENTER_RIGHT
                refreshButton = stretchableButton(
                    STRETCH_POINT,
                    getString(R.string.refresh),
                    ImageView(R.image.btn_refresh_dark)
                ) {
                    styleClass += STYLE_DEFAULT_BUTTON
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