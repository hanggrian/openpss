package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.employee.EmployeeController
import com.hendraanggrian.openpss.ui.invoice.InvoiceController
import com.hendraanggrian.openpss.ui.payment.PaymentController
import com.hendraanggrian.openpss.ui.report.ReportController
import com.hendraanggrian.openpss.ui.report.ReportController.Companion.EXTRA_MAIN_CONTROLLER
import com.hendraanggrian.openpss.ui.schedule.ScheduleController
import com.hendraanggrian.openpss.ui.wage.WageController
import com.hendraanggrian.openpss.utils.getFont
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode.C
import javafx.scene.input.KeyCode.COMMA
import javafx.scene.input.KeyCode.I
import javafx.scene.input.KeyCode.Q
import javafx.scene.input.KeyCode.getKeyCode
import javafx.scene.input.KeyCombination.SHORTCUT_DOWN
import ktfx.application.exit
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.scene.input.plus
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var addInvoiceItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var settingsItem: MenuItem
    @FXML lateinit var navigateMenu: Menu
    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var scheduleController: ScheduleController
    @FXML lateinit var invoiceController: InvoiceController
    @FXML lateinit var paymentController: PaymentController
    @FXML lateinit var reportController: ReportController
    @FXML lateinit var wageController: WageController
    @FXML lateinit var employeeController: EmployeeController

    private lateinit var controllers: List<Controller>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = IS_OS_MAC
        addCustomerItem.accelerator = C + SHORTCUT_DOWN
        addInvoiceItem.accelerator = I + SHORTCUT_DOWN
        settingsItem.accelerator = COMMA + SHORTCUT_DOWN
        quitItem.accelerator = Q + SHORTCUT_DOWN
        navigateMenu.items.forEachIndexed { i, item -> item.accelerator = getKeyCode("${i + 1}") + SHORTCUT_DOWN }

        updateNavigateMenu(tabPane.selectionModel.selectedIndex)
        tabPane.selectionModel.selectedIndexProperty().listener { _, _, index ->
            updateNavigateMenu(index.toInt())
            (controllers[index.toInt()] as? Refreshable)?.refresh()
        }

        later {
            employeeLabel.text = employeeName
            employeeLabel.font = getFont(R.font.opensans_bold)
            controllers = listOf(customerController, scheduleController, invoiceController,
                paymentController, reportController, wageController, employeeController)
            controllers.forEach {
                it._employee = _employee
                when (it) {
                    paymentController, reportController, wageController, employeeController ->
                        controllers.indexOf(it).let { index ->
                            navigateMenu.items[index].isDisable = !isFullAccess
                            tabPane.tabs[index].isDisable = !isFullAccess
                        }
                }
            }
            reportController.addExtra(EXTRA_MAIN_CONTROLLER, this)
        }
    }

    @FXML fun add(event: ActionEvent) = when (event.source) {
        addCustomerItem -> select(customerController) { addCustomer() }
        else -> select(invoiceController) { addInvoice() }
    }

    @FXML fun settings() = SettingsDialog(this, isFullAccess).showAndWait().get()

    @FXML fun quit() = exit()

    @FXML fun navigate(event: ActionEvent) = tabPane.selectionModel.select(navigateMenu.items.indexOf(event.source))

    @FXML fun about() = AboutDialog(this).show()

    fun <T : Controller> select(controller: T, run: T.() -> Unit) {
        tabPane.selectionModel.select(controllers.indexOf(controller))
        controller.run(run)
    }

    private fun updateNavigateMenu(index: Int) = navigateMenu.items.forEachIndexed { i, item ->
        (item as RadioMenuItem).isSelected = index == i
    }
}