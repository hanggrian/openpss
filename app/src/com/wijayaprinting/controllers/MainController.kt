package com.wijayaprinting.controllers

import com.wijayaprinting.base.Refreshable
import com.wijayaprinting.dialogs.AboutDialog
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import kotfx.runFX
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var navigateMenu: Menu

    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var plateController: PlateController
    @FXML lateinit var attendanceController: AttendanceController
    @FXML lateinit var employeeController: EmployeeController

    private lateinit var controllers: Array<Controller>

    @FXML
    override fun initialize() {
        menuBar.isUseSystemMenuBar = IS_OS_MAC

        updateNavigateMenu(tabPane.selectionModel.selectedIndex)
        tabPane.selectionModel.selectedIndexProperty().addListener { _, _, index ->
            updateNavigateMenu(index.toInt())
            (controllers[index.toInt()] as? Refreshable)?.refresh()
        }

        runFX {
            employeeLabel.text = employeeName
            controllers = arrayOf(customerController, plateController, attendanceController, employeeController)
            controllers.forEach {
                it._employee = _employee
                if (it is AttendanceController || it is EmployeeController) {
                    navigateMenu.items[controllers.indexOf(it)].isDisable = !isFullAccess
                    tabPane.tabs[controllers.indexOf(it)].isDisable = !isFullAccess
                }
            }
        }
    }

    @FXML
    fun navigateOnAction(event: ActionEvent) = tabPane.selectionModel.select(navigateMenu.items.indexOf(event.source))

    @FXML
    fun aboutOnAction() {
        AboutDialog(this).showAndWait()
    }

    private fun updateNavigateMenu(index: Int) = navigateMenu.items.forEachIndexed { i, item -> (item as RadioMenuItem).isSelected = index == i }
}