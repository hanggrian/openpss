package com.hanggrian.openpss.ui.schedule

import com.hanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.UncollapsibleTreeItem
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.ui.ActionController
import com.hanggrian.openpss.ui.Refreshable
import com.hanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.ToggleButton
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.image.ImageView
import kotlinx.nosql.equal
import ktfx.bindings.emptyBinding
import ktfx.bindings.or
import ktfx.bindings.stringBindingBy
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxToggleButton
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.borderPane
import ktfx.layouts.tooltip
import ktfx.runLater
import java.net.URL
import java.util.ResourceBundle

class ScheduleController :
    ActionController(),
    Refreshable {
    @FXML
    lateinit var scheduleTable: TreeTableView<Schedule>

    @FXML
    lateinit var jobType: TreeTableColumn<Schedule, String>

    @FXML
    lateinit var descColumn: TreeTableColumn<Schedule, String>

    @FXML
    lateinit var qtyColumn: TreeTableColumn<Schedule, String>

    @FXML
    lateinit var typeColumn: TreeTableColumn<Schedule, String>

    private lateinit var refreshButton: Button
    private lateinit var doneButton: Button
    private lateinit var historyCheck: ToggleButton

    override fun NodeContainer.onCreateActions() {
        refreshButton =
            styledJfxButton(null, ImageView(R.image_act_refresh), R.style_flat) {
                tooltip(getString(R.string_refresh))
                onAction { refresh() }
            }
        doneButton =
            styledJfxButton(null, ImageView(R.image_act_done), R.style_flat) {
                tooltip(getString(R.string_done))
                onAction {
                    transaction {
                        scheduleTable.selectionModel.selectedItem.value.invoice
                            .done(this@ScheduleController)
                    }
                    refresh()
                }
            }
        borderPane {
            minHeight = 50.0
            maxHeight = 50.0
            historyCheck =
                jfxToggleButton {
                    text = getString(R.string_history)
                    selectedProperty().listener { refresh() }
                    doneButton
                        .disableProperty()
                        .bind(
                            scheduleTable.selectionModel.selectedItems.emptyBinding or
                                selectedProperty(),
                        )
                }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        scheduleTable.run {
            root = TreeItem()
            selectionModel.selectionMode = MULTIPLE
            selectionModel.selectedItemProperty().listener { _, _, value ->
                if (value == null) {
                    return@listener
                }
                when {
                    value.children.isEmpty() -> selectionModel.selectAll(value.parent)
                    else -> selectionModel.selectAll(value)
                }
            }
            titleProperty.bind(
                selectionModel.selectedItemProperty().stringBindingBy {
                    Invoice.no(this@ScheduleController, it?.value?.invoice?.no)
                },
            )
        }
        jobType.stringCell { jobType }
        descColumn.stringCell { title }
        qtyColumn.stringCell { qty }
        typeColumn.stringCell { type }
    }

    override fun refresh() =
        runLater {
            scheduleTable.selectionModel.clearSelection()
            scheduleTable.root.children.run {
                clear()
                transaction {
                    when (historyCheck.isSelected) {
                        true -> Invoices { it.isDone.equal(true) }.take(20)
                        else -> Invoices { it.isDone.equal(false) }
                    }.forEach { invoice ->
                        addAll(
                            UncollapsibleTreeItem(
                                Schedule(
                                    invoice,
                                    Customers[invoice.customerId].singleOrNull()?.name.orEmpty(),
                                    "",
                                    "",
                                    invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED),
                                ),
                            ).apply {
                                Schedule
                                    .of(this@ScheduleController, invoice)
                                    .forEach { children += TreeItem(it) }
                            },
                        )
                    }
                }
            }
        }

    private fun <T> TreeTableView.TreeTableViewSelectionModel<T>.selectAll(parent: TreeItem<T>) {
        select(parent)
        parent.children.forEach { select(it) }
    }
}
