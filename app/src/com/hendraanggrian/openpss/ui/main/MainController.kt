package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee.Role.MANAGER
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layout.SegmentedTabPane
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.employee.EmployeeController
import com.hendraanggrian.openpss.ui.finance.FinanceController
import com.hendraanggrian.openpss.ui.invoice.InvoiceController
import com.hendraanggrian.openpss.ui.schedule.ScheduleController
import com.hendraanggrian.openpss.ui.wage.WageController
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.pane
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Modality.APPLICATION_MODAL
import ktfx.application.exit
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.layouts.styledScene
import ktfx.stage.stage
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller(), Selectable<Tab> {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var addInvoiceItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var platePriceItem: MenuItem
    @FXML lateinit var offsetPriceItem: MenuItem
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
    @FXML lateinit var employeeController: EmployeeController

    private lateinit var controllers: List<SegmentedController>

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = IS_OS_MAC
        navigationPane.center = tabPane.header
        AnchorPane.setLeftAnchor(tabPane.header, 0.0)
        AnchorPane.setRightAnchor(tabPane.header, 0.0)

        replaceButtons(customerController)
        selectedIndexProperty.listener { _, _, value ->
            val controller = controllers[value.toInt()]
            replaceButtons(controller)
            if (controller is Refreshable) controller.refresh()
        }

        later {
            controllers = listOf(customerController, invoiceController, scheduleController, financeController,
                wageController, employeeController)
            controllers.forEach { it.login = login }
            financeController.addExtra(FinanceController.EXTRA_MAIN_CONTROLLER, this)
        }
    }

    @FXML fun add(event: ActionEvent) = when (event.source) {
        addCustomerItem -> select(customerController) { later { add() } }
        else -> select(invoiceController) { addInvoice() }
    }

    @FXML fun quit() = exit()

    @FXML fun price(event: ActionEvent) {
        val isPlate = event.source == platePriceItem
        stage(getString(if (isPlate) R.string.plate_price else R.string.offset_price)) {
            initModality(APPLICATION_MODAL)
            val loader = FXMLLoader(getResource(when {
                isPlate -> R.layout.controller_price_plate
                else -> R.layout.controller_price_offset
            }), resources)
            scene = styledScene(getStyle(R.style.openpss), loader.pane)
            isResizable = false
            loader.controller.login = login
        }.show()
    }

    @FXML fun preferences() = PreferencesDialog(this, transaction { login.isAtLeast(MANAGER) }).show()

    @FXML fun about() = AboutDialog(this).show()

    private fun <T : SegmentedController> select(controller: T, run: T.() -> Unit) {
        select(controllers.indexOf(controller))
        controller.run(run)
    }

    private fun replaceButtons(controller: SegmentedController) {
        navigationLeftBox.children.let {
            it.clear()
            it += controller.leftButtons
        }
        navigationRightBox.children.let {
            it.clear()
            it += controller.rightButtons
        }
    }
}