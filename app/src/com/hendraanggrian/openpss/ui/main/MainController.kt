package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layout.SegmentedTabPane
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.finance.FinanceController
import com.hendraanggrian.openpss.ui.invoice.InvoiceController
import com.hendraanggrian.openpss.ui.main.edit.EditEmployeeDialog
import com.hendraanggrian.openpss.ui.main.edit.price.EditOffsetPriceDialog
import com.hendraanggrian.openpss.ui.main.edit.price.EditPlatePriceDialog
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubApi
import com.hendraanggrian.openpss.ui.schedule.ScheduleController
import com.hendraanggrian.openpss.ui.wage.WageController
import com.hendraanggrian.openpss.util.forceExit
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafxx.application.later
import javafxx.coroutines.listener
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.controlsfx.control.NotificationPane
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller(), Selectable<Tab> {

    @FXML lateinit var notificationPane: NotificationPane
    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var addInvoiceItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var platePriceItem: MenuItem
    @FXML lateinit var offsetPriceItem: MenuItem
    @FXML lateinit var employeeItem: MenuItem
    @FXML lateinit var preferencesItem: MenuItem
    @FXML lateinit var navigationPane: BorderPane
    @FXML lateinit var navigationLeftBox: HBox
    @FXML lateinit var navigationRightBox: HBox
    @FXML lateinit var tabPane: SegmentedTabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var invoiceController: InvoiceController
    @FXML lateinit var scheduleController: ScheduleController
    @FXML lateinit var financeController: FinanceController
    @FXML lateinit var wageController: WageController

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel

    private lateinit var controllers: List<SegmentedController>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = IS_OS_MAC
        navigationPane.center = tabPane.header
        AnchorPane.setLeftAnchor(tabPane.header, 0.0)
        AnchorPane.setRightAnchor(tabPane.header, 0.0)

        customerController.replaceButtons()
        selectedIndexProperty.listener { _, _, value ->
            val controller = controllers[value.toInt()]
            controller.replaceButtons()
            (controller as? Refreshable)?.refresh()
        }

        later {
            controllers = listOf(customerController, invoiceController, scheduleController, financeController,
                wageController)
            controllers.forEach { it.employee = employee }
            financeController.addExtra(FinanceController.EXTRA_MAIN_CONTROLLER, this)
        }
    }

    @FXML fun add(event: ActionEvent) = when (event.source) {
        addCustomerItem -> select(customerController) { later { add() } }
        else -> select(invoiceController) { addInvoice() }
    }

    @FXML fun quit() = forceExit()

    @FXML fun editPrice(event: ActionEvent) = when (platePriceItem) {
        event.source -> EditPlatePriceDialog(this, employee)
        else -> EditOffsetPriceDialog(this, employee)
    }.show()

    @FXML fun editEmployee() = EditEmployeeDialog(this, employee).show()

    @FXML fun preferences() = PreferencesDialog(this, transaction { employee.isAdmin() }).show()

    @FXML fun about() = AboutDialog(this).show()

    @FXML fun checkUpdate() = GitHubApi.checkUpdates(this, { title, actions ->
        notificationPane.text = title
        notificationPane.actions.setAll(actions)
        notificationPane.show()
    }) { _, content ->
        notificationPane.text = content
        notificationPane.actions.clear()
        notificationPane.show()
    }

    private fun <T : SegmentedController> select(controller: T, run: T.() -> Unit) {
        select(controllers.indexOf(controller))
        controller.run(run)
    }

    private fun SegmentedController.replaceButtons() {
        navigationLeftBox.children.let {
            it.clear()
            it += leftButtons
        }
        navigationRightBox.children.let {
            it.clear()
            it += rightButtons
        }
    }
}