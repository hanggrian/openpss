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
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode.C
import javafx.scene.input.KeyCode.Q
import javafx.scene.input.KeyCode.getKeyCode
import javafx.scene.input.KeyCombination.SHORTCUT_DOWN
import ktfx.application.exit
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.scene.input.plus
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var navigateMenu: Menu

    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var controllers: List<Controller>
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var orderController: OrderController
    @FXML lateinit var wageController: WageController
    @FXML lateinit var employeeController: EmployeeController

    override fun initialize() {
        menuBar.isUseSystemMenuBar = IS_OS_MAC
        addCustomerItem.accelerator = C + SHORTCUT_DOWN
        quitItem.accelerator = Q + SHORTCUT_DOWN
        navigateMenu.items.forEachIndexed { index, item ->
            item.accelerator = getKeyCode("${index + 1}") + SHORTCUT_DOWN
        }

        updateNavigateMenu(tabPane.selectionModel.selectedIndex)
        tabPane.selectionModel.selectedIndexProperty().listener { _, _, index ->
            updateNavigateMenu(index.toInt())
            (controllers[index.toInt()] as? Refreshable)?.refresh()
        }

        later {
            employeeLabel.text = employeeName
            controllers.forEach {
                it._employee = _employee
                if (it == wageController || it == employeeController) {
                    navigateMenu.items[controllers.indexOf(it)].isDisable = !isFullAccess
                    tabPane.tabs[controllers.indexOf(it)].isDisable = !isFullAccess
                }
            }
        }
    }

    @FXML fun addCustomer() = customerController.add()

    @FXML fun quit() = exit()

    @FXML fun navigate(event: ActionEvent) = tabPane.selectionModel.select(navigateMenu.items.indexOf(event.source))

    @FXML fun about() {
        AboutDialog(this).showAndWait()
    }

    private fun updateNavigateMenu(index: Int) = navigateMenu.items.forEachIndexed { i, item ->
        (item as RadioMenuItem).isSelected = index == i
    }
}