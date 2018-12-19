package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.App.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.StretchableButton
import com.hendraanggrian.openpss.control.UncollapsibleTreeItem
import com.hendraanggrian.openpss.db.schema.no
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.ToggleButton
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.application.later
import ktfx.beans.binding.buildStringBinding
import ktfx.beans.value.or
import ktfx.collections.isEmptyBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxToggleButton
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import java.net.URL
import java.util.ResourceBundle

class ScheduleController : ActionController(), Refreshable {

    @FXML lateinit var scheduleTable: TreeTableView<Schedule>
    @FXML lateinit var jobType: TreeTableColumn<Schedule, String>
    @FXML lateinit var descColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var typeColumn: TreeTableColumn<Schedule, String>

    private lateinit var refreshButton: Button
    private lateinit var doneButton: Button
    private lateinit var historyCheck: ToggleButton

    override fun NodeInvokable.onCreateActions() {
        refreshButton = StretchableButton(
            STRETCH_POINT,
            getString(R.string.refresh),
            ImageView(R.image.act_refresh)
        ).apply {
            onAction { refresh() }
        }()
        doneButton = StretchableButton(
            STRETCH_POINT,
            getString(R.string.done),
            ImageView(R.image.act_done)
        ).apply {
            onAction {
                api.editInvoice(scheduleTable.selectionModel.selectedItem.value.invoice, isDone = true)
                refresh()
            }
        }()
        borderPane {
            minHeight = 50.0
            maxHeight = 50.0
            historyCheck = jfxToggleButton {
                text = getString(R.string.history)
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

    override fun refresh() = later {
        scheduleTable.selectionModel.clearSelection()
        scheduleTable.root.children.run {
            clear()
            GlobalScope.launch(Dispatchers.JavaFx) {
                when (historyCheck.isSelected) {
                    true -> api.getInvoices(isDone = true, page = 1, count = 20).items
                    else -> api.getInvoices(isDone = false, page = 1, count = 100).items
                }.forEach { invoice ->
                    addAll(UncollapsibleTreeItem(
                        Schedule(
                            invoice, api.getCustomer(invoice.customerId).name, "", "",
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