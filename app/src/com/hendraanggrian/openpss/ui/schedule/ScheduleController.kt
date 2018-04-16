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
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.booleanBindingOf
import ktfx.layouts.label
import ktfx.listeners.cellFactory
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
            columns.forEach {
                it.cellFactory {
                    onUpdate { any, empty ->
                        graphic = null
                        if (any != null && !empty) graphic = label(any.toString()) {
                            font = com.hendraanggrian.openpss.utils.getFont(when {
                                treeTableRow.treeItem?.value?.isNode() ?: true -> R.font.opensans_bold
                                else -> R.font.opensans_regular
                            })
                        }
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
                Invoices.find { done.equal(false) }.forEach {
                    addAll(UncollapsibleTreeItem(Schedule(it.id, it.dateTime.toString(PATTERN_DATETIME_EXTENDED),
                        findById(Customers, it.customerId).single().name)).apply {
                        it.plates.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(null, getString(R.string.plate), it.title, it.qty, it.type))
                        }
                        it.offsets.forEach {
                            children += TreeItem<Schedule>(
                                Schedule(null, getString(R.string.offset), it.title, it.qty, it.type))
                        }
                        it.others.forEach {
                            children += TreeItem<Schedule>(Schedule(null, getString(R.string.others), it.title, it.qty))
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
}