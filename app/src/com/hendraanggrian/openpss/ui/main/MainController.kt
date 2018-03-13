package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.employee.EmployeeController
import com.hendraanggrian.openpss.ui.order.OrderController
import com.hendraanggrian.openpss.ui.wage.WageController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.TabPane
import kfx.application.later
import kfx.coroutines.listener
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var navigateMenu: Menu

    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var orderController: OrderController
    @FXML lateinit var wageController: WageController
    @FXML lateinit var employeeController: EmployeeController

    private lateinit var controllers: Array<Controller>

    override fun initialize() {
        menuBar.isUseSystemMenuBar = IS_OS_MAC

        updateNavigateMenu(tabPane.selectionModel.selectedIndex)
        tabPane.selectionModel.selectedIndexProperty().listener { _, _, index ->
            updateNavigateMenu(index.toInt())
            (controllers[index.toInt()] as? Refreshable)?.refresh()
        }

        later {
            employeeLabel.text = employeeName
            controllers = arrayOf(customerController, orderController, wageController, employeeController)
            controllers.forEach {
                it._employee = _employee
                if (it is WageController || it is EmployeeController) {
                    navigateMenu.items[controllers.indexOf(it)].isDisable = !isFullAccess
                    tabPane.tabs[controllers.indexOf(it)].isDisable = !isFullAccess
                }
            }
        }
    }

    @FXML fun addCustomer() = customerController.add()

    @FXML fun exit() = kfx.application.exit()

    @FXML fun navigate(event: ActionEvent) = tabPane.selectionModel.select(navigateMenu.items.indexOf(event.source))

    @FXML fun about() = AboutDialog(this).showAndWait().get()

    private fun updateNavigateMenu(index: Int) = navigateMenu.items.forEachIndexed { i, item ->
        (item as RadioMenuItem).isSelected = index == i
    }
}