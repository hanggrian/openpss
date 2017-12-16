package com.wijayaprinting.manager.controller

import javafx.fxml.FXML
import javafx.scene.control.MenuBar
import javafx.scene.control.TabPane
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC

class MainController {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        if (IS_OS_MAC) {
            menuBar.isUseSystemMenuBar = true
        }
    }
}