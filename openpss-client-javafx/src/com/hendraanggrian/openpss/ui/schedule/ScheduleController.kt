package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.PATTERN_DATETIMEEXT
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.Action
import com.hendraanggrian.openpss.control.UncollapsibleTreeItem
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.no
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.util.stringCell
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.ToggleButton
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.buildStringBinding
import ktfx.bindings.or
import ktfx.collections.isEmptyBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxToggleButton
import ktfx.layouts.NodeManager
import ktfx.layouts.addNode
import ktfx.layouts.borderPane
import ktfx.runLater

class ScheduleController : ActionController(), Refreshable {

    @FXML lateinit var scheduleTable: TreeTableView<Schedule>
    @FXML lateinit var jobType: TreeTableColumn<Schedule, String>
    @FXML lateinit var descColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var typeColumn: TreeTableColumn<Schedule, String>

    private lateinit var refreshButton: Button
    private lateinit var doneButton: Button
    private lateinit var historyCheck: ToggleButton

    override fun NodeManager.onCreateActions() {
        refreshButton = addNode(Action(getString(R2.string.refresh), R.image.action_refresh)) {
            onAction { refresh() }
        }
        doneButton = addNode(Action(getString(R2.string.done), R.image.action_done)) {
            onAction {
                OpenPSSApi.editInvoice(
                    scheduleTable.selectionModel.selectedItem.value.invoice.apply {
                        isDone = true
                    }
                )
                refresh()
            }
        }
        borderPane {
            minHeight = 50.0
            maxHeight = 50.0
            historyCheck = jfxToggleButton {
                text = getString(R2.string.history)
                selectedProperty().listener { refresh() }
                doneButton.disableProperty()
                    .bind(scheduleTable.selectionModel.selectedItems.isEmptyBinding or selectedProperty())
            }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        scheduleTable.run {
            root = TreeItem()
            selectionModel.selectionMode = MULTIPLE
            selectionModel.selectedItemProperty().listener { _, _, value ->
                if (value != null) when {
                    value.children.isEmpty() -> selectionModel.selectAll(value.parent)
                    else -> selectionModel.selectAll(value)
                }
            }
            titleProperty().bind(buildStringBinding(selectionModel.selectedItemProperty()) {
                Invoice.no(this@ScheduleController, selectionModel.selectedItem?.value?.invoice?.no)
            })
        }
        jobType.stringCell { jobType }
        descColumn.stringCell { title }
        qtyColumn.stringCell { qty }
        typeColumn.stringCell { type }
    }

    override fun refresh() = runLater {
        scheduleTable.selectionModel.clearSelection()
        scheduleTable.root.children.run {
            clear()
            runBlocking {
                when (historyCheck.isSelected) {
                    true -> OpenPSSApi.getInvoices(isDone = true, page = 1, count = 20).items
                    else -> OpenPSSApi.getInvoices(isDone = false, page = 1, count = 100).items
                }
            }.forEach { invoice ->
                addAll(
                    UncollapsibleTreeItem(
                        Schedule(
                            invoice,
                            runBlocking(Dispatchers.IO) {
                                OpenPSSApi.getCustomer(invoice.customerId).name
                            },
                            "",
                            "",
                            invoice.dateTime.toString(PATTERN_DATETIMEEXT)
                        )
                    ).apply {
                        Schedule.of(this@ScheduleController, invoice)
                            .forEach { children += TreeItem<Schedule>(it) }
                    })
            }
        }
    }

    private fun <S> TreeTableView.TreeTableViewSelectionModel<S>.selectAll(parent: TreeItem<S>) {
        select(parent)
        parent.children.forEach { select(it) }
    }
}
