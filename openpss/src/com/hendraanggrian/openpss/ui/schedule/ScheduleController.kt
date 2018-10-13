package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.UncollapsibleTreeItem
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.stretchableToggleButton
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.control.SegmentedTabPane.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.TreeSelectable
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.ToggleButton
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel
import javafx.scene.image.ImageView
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.value.or
import ktfx.collections.isEmpty
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutManager
import java.net.URL
import java.util.ResourceBundle

class ScheduleController : SegmentedController(), Refreshable, TreeSelectable<Schedule> {

    @FXML lateinit var scheduleTable: TreeTableView<Schedule>
    @FXML lateinit var typeColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var titleColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var machineColumn: TreeTableColumn<Schedule, String>

    private lateinit var refreshButton: Button
    private lateinit var doneButton: Button
    private lateinit var historyToggle: ToggleButton

    override val selectionModel: TreeTableViewSelectionModel<Schedule> get() = scheduleTable.selectionModel

    override fun LayoutManager<Node>.onCreateLeftActions() {
        refreshButton =
            stretchableButton(STRETCH_POINT, getString(R.string.refresh), ImageView(R.image.btn_refresh_light)) {
                onAction { refresh() }
            }
        doneButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.done),
            ImageView(R.image.btn_done_dark)
        ) {
            styleClass += STYLE_DEFAULT_BUTTON
            onAction { done() }
        }
    }

    override fun LayoutManager<Node>.onCreateRightActions() {
        historyToggle = stretchableToggleButton(STRETCH_POINT, R.string.history, ImageView(R.image.btn_history_light)) {
            selectedProperty().listener { refresh() }
            doneButton.disableProperty().bind(selecteds.isEmpty or selectedProperty())
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
        typeColumn.stringCell { firstColumn }
        titleColumn.stringCell { title }
        qtyColumn.stringCell { qty }
        machineColumn.stringCell { type }
    }

    override fun refresh() = later {
        clearSelection()
        scheduleTable.root.children.run {
            clear()
            transaction {
                when (historyToggle.isSelected) {
                    true -> Invoices { it.done.equal(true) }.take(20)
                    else -> Invoices { it.done.equal(false) }
                }.forEach { invoice ->
                    addAll(UncollapsibleTreeItem(
                        Schedule(
                            invoice.id, Customers[invoice.customerId].single().name, "", "",
                            invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED)
                        )
                    ).apply {
                        invoice.plates.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(invoice.id, getString(R.string.plate), it.title, it.qty, it.machine)
                            )
                        }
                        invoice.offsets.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(invoice.id, getString(R.string.offset), it.title, it.qty, it.machine)
                            )
                        }
                        invoice.others.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(invoice.id, getString(R.string.others), it.title, it.qty)
                            )
                        }
                    })
                }
            }
        }
    }

    private fun done() {
        transaction { Invoices[selected!!.value.invoiceId!!].projection { done }.update(true) }
        refresh()
    }

    private fun <S> TreeTableView.TreeTableViewSelectionModel<S>.selectAll(parent: TreeItem<S>) {
        select(parent)
        parent.children.forEach { select(it) }
    }
}