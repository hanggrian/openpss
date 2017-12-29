package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.dialog.AboutDialog
import javafx.fxml.FXML
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuBar
import javafx.scene.control.TabPane
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var customerMenu: CheckMenuItem
    @FXML lateinit var receiptMenu: CheckMenuItem
    @FXML lateinit var attendanceMenu: CheckMenuItem

    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var receiptController: ReceiptController
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        menuBar.isUseSystemMenuBar = IS_OS_MAC
        tabPane.selectionModel.selectedIndexProperty().addListener { _, _, index -> updateNavigation(index.toInt()) }
        updateNavigation(tabPane.selectionModel.selectedIndex)
    }

    @FXML fun customerOnAction() = tabPane.selectionModel.select(0)
    @FXML fun receiptOnAction() = tabPane.selectionModel.select(1)
    @FXML fun attendanceOnAction() = tabPane.selectionModel.select(2)

    @FXML
    fun aboutOnAction() {
        AboutDialog(resources).showAndWait()
    }

    private fun updateNavigation(index: Int) {
        customerMenu.isSelected = false
        receiptMenu.isSelected = false
        attendanceMenu.isSelected = false
        when (index) {
            0 -> customerMenu.isSelected = true
            1 -> receiptMenu.isSelected = true
            2 -> attendanceMenu.isSelected = true
        }
    }
}