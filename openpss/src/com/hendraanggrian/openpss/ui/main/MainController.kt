package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoice.Offset.Technique.TWO_SIDE_EQUAL
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
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
import com.hendraanggrian.openpss.ui.wage.EditRecessDialog
import com.hendraanggrian.openpss.ui.wage.WageController
import com.hendraanggrian.openpss.util.forceExit
import com.jfoenix.controls.JFXHamburger
import com.jfoenix.controls.JFXTabPane
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import kotlinx.coroutines.experimental.delay
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.scene.control.errorAlert
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.controlsfx.control.NotificationPane
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller(), Selectable<Tab> {

    @FXML override lateinit var dialogContainer: StackPane // jfx dialog container
    @FXML lateinit var notificationPane: NotificationPane
    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var addInvoiceItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var platePriceItem: MenuItem
    @FXML lateinit var offsetPriceItem: MenuItem
    @FXML lateinit var employeeItem: MenuItem
    @FXML lateinit var recessItem: MenuItem
    @FXML lateinit var preferencesItem: MenuItem
    @FXML lateinit var tabPane: JFXTabPane
    @FXML lateinit var hamburger: JFXHamburger
    @FXML lateinit var actionBox: HBox
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var invoiceController: InvoiceController
    @FXML lateinit var scheduleController: ScheduleController
    @FXML lateinit var financeController: FinanceController
    @FXML lateinit var wageController: WageController

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel
    private val controllers
        get() = mutableListOf(
            customerController,
            invoiceController,
            scheduleController,
            financeController,
            wageController
        )

    private var isFinanceTabFixed = false

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = IS_OS_MAC

        val burgerTask = HamburgerSlideCloseTransition(hamburger)
        burgerTask.rate = -1.0
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED) { _ ->
            burgerTask.rate = burgerTask.rate * -1
            burgerTask.play()
        }

        customerController.replaceButtons()
        selectedIndexProperty.listener { _, _, value ->
            val controller = controllers[value.toInt()]
            controller.replaceButtons()
            if (controller is Refreshable) {
                controller.refresh()
                if (controller is FinanceController && !isFinanceTabFixed) {
                    fixFinanceTab(1)
                    fixFinanceTab(0)
                    isFinanceTabFixed = true
                }
            }
        }

        later {
            controllers.forEach {
                it.employee = employee
                it.dialogContainer = dialogContainer
            }
            financeController.addExtra(FinanceController.EXTRA_MAIN_CONTROLLER, this)
        }
    }

    @FXML fun add(event: ActionEvent) = when (event.source) {
        addCustomerItem -> select(customerController) { later { add() } }
        else -> select(invoiceController) { addInvoice() }
    }

    @FXML fun quit() = forceExit()

    @FXML fun editPrice(event: ActionEvent) = when (event.source) {
        platePriceItem -> EditPlatePriceDialog(this, employee)
        else -> EditOffsetPriceDialog(this, employee)
    }.show(dialogContainer)

    @FXML fun editEmployee() = EditEmployeeDialog(this, employee).show(dialogContainer)

    @FXML fun editRecess() = EditRecessDialog(this, employee).show(dialogContainer)

    @FXML fun preferences() = PreferencesDialog(this, transaction { employee.isAdmin() }).show(dialogContainer)

    @FXML fun testViewInvoice() {
        val customer = transaction { Customers.find().firstOrNull() }
        when (customer) {
            null -> errorAlert(getString(R.string.no_customer_to_test)).showAndWait()
            else -> ViewInvoicePopover(
                Invoice(
                    no = 1234,
                    employeeId = employee.id,
                    customerId = customer.id,
                    dateTime = dbDateTime,
                    plates = listOf(Invoice.Plate.new("Title", 5, 92000.0, "Machine")),
                    offsets = listOf(Invoice.Offset.new("Title", 5, 92000.0, "Machine", TWO_SIDE_EQUAL)),
                    others = listOf(Invoice.Other.new("Title", 5, 92000.0)),
                    note = "This is a test",
                    printed = false,
                    paid = false,
                    done = false
                ), true
            ).show(menuBar)
        }
    }

    @FXML fun checkUpdate() = GitHubApi.checkUpdates(this, { title, actions ->
        notificationPane.text = title
        notificationPane.actions.setAll(actions)
        notificationPane.show()
    }) { _, content ->
        notificationPane.text = content
        notificationPane.actions.clear()
        notificationPane.show()
    }

    @FXML fun about() = AboutDialog(this).show()

    private fun ActionController.replaceButtons() = actionBox.children.setAll(actionManager.collection)

    private inline fun <T : ActionController> select(controller: T, run: T.() -> Unit) {
        select(controllers.indexOf(controller))
        controller.run(run)
    }

    private suspend inline fun fixFinanceTab(index: Int) {
        delay(100)
        financeController.tabPane.header.buttons[index].requestFocus()
    }
}