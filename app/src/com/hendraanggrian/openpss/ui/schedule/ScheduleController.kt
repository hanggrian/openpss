package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UncollapsibleTreeItem
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import kotlinx.nosql.equal
import ktfx.application.later
import java.net.URL
import java.util.ResourceBundle

class ScheduleController : Controller(), Refreshable, Selectable<Schedule> {

    @FXML lateinit var doneButton: Button
    @FXML lateinit var scheduleTable: TreeTableView<Schedule>
    @FXML lateinit var orderColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var titleColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    /*@FXML lateinit var typeColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var employeeColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var customerColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var qtyColumn: TreeTableColumn<Schedule, String>
    @FXML lateinit var descriptionColumn: TreeTableColumn<Schedule, String>*/

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        doneButton.disableProperty().bind(!selectedBinding)
        /*typeColumn.stringCell { type.asString(this@ScheduleController) }
        employeeColumn.stringCell { employee }
        customerColumn.stringCell { customer }
        qtyColumn.numberCell { order.qty }
        descriptionColumn.stringCell { description }*/
        orderColumn.stringCell { firstColumn }
        titleColumn.stringCell { title }
        qtyColumn.stringCell { qty }
    }

    override fun refresh() = later {
        scheduleTable.root = TreeItem<Schedule>().apply {
            transaction {
                Invoices.find { done.equal(false) }.forEach {
                    children +=
                        UncollapsibleTreeItem(Schedule(it.dateTime.toString(PATTERN_DATETIME_EXTENDED), "ASDASD"))
                            .apply {
                                it.plates.forEach {
                                    children +=
                                        TreeItem<Schedule>(Schedule(getString(R.string.plate), it.title, it.qty))
                                }
                                it.offsets.forEach {
                                    children +=
                                        TreeItem<Schedule>(Schedule(getString(R.string.offset), it.title, it.qty))
                                }
                                it.others.forEach {
                                    children +=
                                        TreeItem<Schedule>(Schedule(getString(R.string.others), it.title, it.qty))
                                }
                            }
                }
            }
        }
        // scheduleTable.items = transaction { Schedule.listAll(Invoices.find { done.equal(false) }, typeChoice.value) }
    }

    override val selectionModel: SelectionModel<Schedule>
        @Suppress("UNCHECKED_CAST") get() = scheduleTable.selectionModel as SelectionModel<Schedule>

    @FXML fun done() {
        //transaction { findById(Invoices, selected!!.invoiceId).projection { done }.update(true) }
        refresh()
    }
}