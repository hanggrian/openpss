package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.layouts.SegmentedTabPane
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.employee.EmployeeController
import com.hendraanggrian.openpss.ui.finance.FinanceController
import com.hendraanggrian.openpss.ui.invoice.InvoiceController
import com.hendraanggrian.openpss.ui.report.ReportController
import com.hendraanggrian.openpss.ui.schedule.ScheduleController
import com.hendraanggrian.openpss.ui.wage.WageController
import javafx.fxml.FXML
import ktfx.application.later
import ktfx.coroutines.listener
import java.net.URL
import java.util.ResourceBundle

class MainController2 : Controller() {

    @FXML lateinit var pane: SegmentedTabPane
    @Suppress("MemberVisibilityCanBePrivate") @FXML lateinit var customerController: CustomerController
    @Suppress("MemberVisibilityCanBePrivate") @FXML lateinit var scheduleController: ScheduleController
    @Suppress("MemberVisibilityCanBePrivate") @FXML lateinit var invoiceController: InvoiceController
    @Suppress("MemberVisibilityCanBePrivate") @FXML lateinit var financeController: FinanceController
    @Suppress("MemberVisibilityCanBePrivate") @FXML lateinit var wageController: WageController
    @Suppress("MemberVisibilityCanBePrivate") @FXML lateinit var employeeController: EmployeeController

    private lateinit var controllers: List<SegmentedController>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        replaceButtons(customerController)
        pane.selectionModel.selectedIndexProperty().listener { _, _, value ->
            val controller = controllers[value.toInt()]
            replaceButtons(controller)
            if (controller is Refreshable) controller.refresh()
        }

        later {
            // employeeLabel.text = login.name
            controllers = listOf(customerController, scheduleController, invoiceController, financeController,
                wageController, employeeController)
            controllers.forEach { it.login = login }
            financeController.addExtra(ReportController.EXTRA_MAIN_CONTROLLER, this)
        }
    }

    private fun replaceButtons(controller: SegmentedController) {
        pane.leftButtons.let {
            it.clear()
            it += controller.leftButtons
        }
        pane.rightButtons.let {
            it.clear()
            it += controller.rightButtons
        }
    }
}