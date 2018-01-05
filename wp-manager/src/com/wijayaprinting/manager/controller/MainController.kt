package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.App
import com.wijayaprinting.manager.dialog.AboutDialog
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

    private lateinit var controllers: Array<Controller>

    @FXML
    fun initialize() {
        navigateMenu.items[2].isDisable = !App.fullAccess
        tabPane.tabs[2].isDisable = !App.fullAccess
        employeeLabel.text = App.employee

        menuBar.isUseSystemMenuBar = IS_OS_MAC
        navigateMenu(tabPane.selectionModel.selectedIndex)
        runFX {
            controllers = arrayOf(customerController, plateController, attendanceController)
            tabPane.selectionModel.selectedIndexProperty().addListener { _, _, newIndex -> navigateMenu(newIndex.toInt()) }
        }
    }

    @FXML fun navigateCustomerOnAction() = tabPane.selectionModel.select(0)
    @FXML fun navigatePlateOnAction() = tabPane.selectionModel.select(1)
    @FXML fun navigateAttendanceOnAction() = tabPane.selectionModel.select(2)

    @FXML
    fun aboutOnAction() {
        AboutDialog(this).showAndWait()
    }

    private fun navigateMenu(index: Int) = navigateMenu.items.forEachIndexed { i, item -> (item as CheckMenuItem).isSelected = index == i }
}