package com.hendraanggrian.openpss.ui.payment

import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.Payment
import com.hendraanggrian.openpss.db.schema.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.utils.currencyCell
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.nosql.equal
import kotlinx.nosql.id
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import java.net.URL
import java.util.ResourceBundle

class PaymentController : Controller(), Refreshable {

    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var paymentTable: TableView<Payment>
    @FXML lateinit var idColumn: TableColumn<Payment, String>
    @FXML lateinit var dateColumn: TableColumn<Payment, String>
    @FXML lateinit var employeeColumn: TableColumn<Payment, String>
    @FXML lateinit var valueColumn: TableColumn<Payment, String>
    @FXML lateinit var methodColumn: TableColumn<Payment, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        dateBox.dateProperty.listener { refresh() }
        idColumn.stringCell { id }
        dateColumn.stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) }
        employeeColumn.stringCell { transaction { Employees.find { id.equal(employeeId) }.single() }!! }
        valueColumn.currencyCell { value }
        methodColumn.stringCell { getTransferDisplayText(this@PaymentController) }
    }

    override fun refresh() {
        paymentTable.items = transaction {
            Payments.find { dateTime.matches(dateBox.date.toString().toPattern()) }.toMutableObservableList()
        }
    }
}