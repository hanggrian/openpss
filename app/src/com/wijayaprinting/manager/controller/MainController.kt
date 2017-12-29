package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.dialog.AboutDialog
import javafx.fxml.FXML
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController : Controller() {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var aboutMenu: MenuItem

    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        if (IS_OS_MAC) menuBar.isUseSystemMenuBar = true
    }

    @FXML
    fun aboutOnAction() = AboutDialog(resources).showAndWait()
}