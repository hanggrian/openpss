package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.nosql.equal
import java.net.URL
import java.util.ResourceBundle

class ScheduleController : Controller(), Refreshable {

    @FXML lateinit var typeChoice: ChoiceBox<String>
    @FXML lateinit var scheduleTable: TableView<Schedule>
    @FXML lateinit var typeColumn: TableColumn<Schedule, String>
    @FXML lateinit var titleColumn: TableColumn<Schedule, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        typeColumn.stringCell { type }
        titleColumn.stringCell { title }
    }

    override fun refresh() {
        scheduleTable.items = transaction { Schedule.from(Invoices.find { done.equal(false) }) }
    }
}