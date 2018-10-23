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
import com.hendraanggrian.openpss.ui.Selectable2
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
import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.JFXHamburger
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXToolbar
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition
import javafx.animation.Animation
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import kotlinx.coroutines.experimental.delay
import ktfx.application.later
import ktfx.beans.binding.stringBindingOf
import ktfx.coroutines.listener
import ktfx.scene.control.errorAlert
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.controlsfx.control.NotificationPane
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller(), Selectable<Tab>, Selectable2<Label> {

    @FXML override lateinit var dialogContainer: StackPane
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
    @FXML lateinit var drawer: JFXDrawer
    @FXML lateinit var drawerList: JFXListView<Label>
    @FXML lateinit var toolbar: JFXToolbar
    @FXML lateinit var hamburger: JFXHamburger
    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var titleLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var invoiceController: InvoiceController
    @FXML lateinit var scheduleController: ScheduleController
    @FXML lateinit var financeController: FinanceController
    @FXML lateinit var wageController: WageController

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel
    override val selectionModel2: SelectionModel<Label> get() = drawerList.selectionModel

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

        selectFirst2()
        selectedProperty2.listener { _, _, _ ->
            select(selectedIndex2)
            drawer.toggle()
        }
        titleLabel.textProperty().bind(stringBindingOf(selectedProperty) { selected!!.text })

        val transition = HamburgerSlideCloseTransition(hamburger).apply { rate = -1.0 }
        drawer.setOnDrawerOpening { transition.toggle() }
        drawer.setOnDrawerClosing { transition.toggle() }
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED) { _ -> drawer.toggle() }

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
            employeeLabel.text = employee.name
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

    private fun Animation.toggle() {
        rate *= -1
        play()
    }

    private fun ActionController.replaceButtons() = toolbar.setRightItems(*actionManager.collection.toTypedArray())

    private inline fun <T : ActionController> select(controller: T, run: T.() -> Unit) {
        select(controllers.indexOf(controller))
        controller.run(run)
    }

    private suspend inline fun fixFinanceTab(index: Int) {
        delay(100)
        financeController.tabPane.header.buttons[index].requestFocus()
    }
}