package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UncollapsibleTreeItem
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.TreeSelectable
import com.hendraanggrian.openpss.utils.findById
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.booleanBindingOf
import ktfx.coroutines.listener
import java.net.URL
import java.util.ResourceBundle

class ScheduleController : Controller(), Refreshable, TreeSelectable<Schedule> {

    @FXML lateinit var doneButton: Button
    @FXML lateinit var scheduleTable: TreeTableView<Schedule>
    @FXML lateinit var orderColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var titleColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var typeColumn: TreeTableColumn<Schedule, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        doneButton.disableProperty().bind(booleanBindingOf(selectedProperty) {
            selected?.let { scheduleTable.root != it && it.value?.isChild() ?: false } ?: true
        })
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
        orderColumn.stringCell { firstColumn }
        titleColumn.stringCell { title }
        qtyColumn.stringCell { qty }
        typeColumn.stringCell { type }
    }

    override fun refresh() = later {
        scheduleTable.root.children.run {
            clear()
            transaction {
                Invoices.find { done.equal(false) }.forEach { invoice ->
                    addAll(UncollapsibleTreeItem(
                        Schedule(invoice.id, invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED),
                            findById(Customers, invoice.customerId).single().name)).apply {
                        invoice.plates.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(invoice.id, getString(R.string.plate), it.title, it.qty, it.type))
                        }
                        invoice.offsets.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(invoice.id, getString(R.string.offset), it.title, it.qty, it.type))
                        }
                        invoice.others.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(invoice.id, getString(R.string.others), it.title, it.qty))
                        }
                    })
                }
            }
        }
    }

    override val selectionModel: TreeTableViewSelectionModel<Schedule> get() = scheduleTable.selectionModel

    @FXML fun done() {
        transaction { findById(Invoices, selected!!.value.invoiceId!!).projection { done }.update(true) }
        refresh()
    }

    private fun <S> TreeTableView.TreeTableViewSelectionModel<S>.selectAll(parent: TreeItem<S>) {
        select(parent)
        parent.children.forEach { select(it) }
    }
}