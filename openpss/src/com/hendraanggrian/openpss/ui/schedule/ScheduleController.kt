package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.App.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.control.UncollapsibleTreeItem
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.TreeSelectable
import com.hendraanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.ToggleButton
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel
import javafx.scene.image.ImageView
import kotlinx.nosql.equal
import ktfx.application.later
import ktfx.beans.value.or
import ktfx.collections.isEmptyBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxToggleButton
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import java.net.URL
import java.util.ResourceBundle

class ScheduleController : ActionController(), Refreshable, TreeSelectable<Schedule> {

    @FXML lateinit var scheduleTable: TreeTableView<Schedule>
    @FXML lateinit var jobType: TreeTableColumn<Schedule, String>
    @FXML lateinit var descColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var typeColumn: TreeTableColumn<Schedule, String>

    private lateinit var refreshButton: Button
    private lateinit var doneButton: Button
    private lateinit var historyCheck: ToggleButton

    override val selectionModel: TreeTableViewSelectionModel<Schedule> get() = scheduleTable.selectionModel

    override fun NodeInvokable.onCreateActions() {
        refreshButton = stretchableButton(STRETCH_POINT, getString(R.string.refresh), ImageView(R.image.act_refresh)) {
            onAction { refresh() }
        }
        doneButton = stretchableButton(STRETCH_POINT, getString(R.string.done), ImageView(R.image.act_done)) {
            onAction {
                (DoneAction(this@ScheduleController, selected!!.value.invoice)) {
                    refresh()
                }
            }
        }
        borderPane {
            minHeight = 50.0
            maxHeight = 50.0
            historyCheck = jfxToggleButton {
                text = getString(R.string.history)
                selectedProperty().listener { refresh() }
                doneButton.disableProperty().bind(selecteds.isEmptyBinding or selectedProperty())
            }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        scheduleTable.run {
            root = TreeItem()
            selectionModel.run {
                selectionMode = MULTIPLE
                selectedItemProperty().listener { _, _, value ->
                    if (value != null) when {
                        value.children.isEmpty() -> selectAll(value.parent)
                        else -> selectAll(value)
                    }
                }
            }
        }
        jobType.stringCell { jobType }
        descColumn.stringCell { title }
        qtyColumn.stringCell { qty }
        typeColumn.stringCell { type }
    }

    override fun refresh() = later {
        clearSelection()
        scheduleTable.root.children.run {
            clear()
            transaction {
                when (historyCheck.isSelected) {
                    true -> Invoices { it.isDone.equal(true) }.take(20)
                    else -> Invoices { it.isDone.equal(false) }
                }.forEach { invoice ->
                    addAll(UncollapsibleTreeItem(
                        Schedule(
                            invoice, Customers[invoice.customerId].single().name, "", "",
                            invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED)
                        )
                    ).apply {
                        Schedule.of(this@ScheduleController, invoice).forEach { children += TreeItem<Schedule>(it) }
                    })
                }
            }
        }
    }

    private fun <S> TreeTableView.TreeTableViewSelectionModel<S>.selectAll(parent: TreeItem<S>) {
        select(parent)
        parent.children.forEach { select(it) }
    }
}