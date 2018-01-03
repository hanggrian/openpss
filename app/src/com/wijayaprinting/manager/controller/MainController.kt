package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.App
import com.wijayaprinting.manager.dialog.AboutDialog
import javafx.fxml.FXML
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuBar
import javafx.scene.control.TabPane
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var customerMenu: CheckMenuItem
    @FXML lateinit var plateMenu: CheckMenuItem
    @FXML lateinit var attendanceMenu: CheckMenuItem

    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var plateController: PlateController
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        attendanceMenu.isDisable = !App.fullAccess
        tabPane.tabs[2].isDisable = !App.fullAccess

        menuBar.isUseSystemMenuBar = IS_OS_MAC
        tabPane.selectionModel.selectedIndexProperty().addListener { _, _, index -> updateNavigation(index.toInt()) }
        updateNavigation(tabPane.selectionModel.selectedIndex)
    }

    @FXML fun customerOnAction() = tabPane.selectionModel.select(0)
    @FXML fun plateOnAction() = tabPane.selectionModel.select(1)
    @FXML fun attendanceOnAction() = tabPane.selectionModel.select(2)

    @FXML
    fun aboutOnAction() {
        AboutDialog(resources).showAndWait()
    }

    private fun updateNavigation(index: Int) = arrayOf(customerMenu, plateMenu, attendanceMenu).forEachIndexed { i, item -> item.isSelected = index == i }
}