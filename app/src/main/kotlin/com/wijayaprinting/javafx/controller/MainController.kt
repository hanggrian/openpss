package com.wijayaprinting.javafx.controller

import javafx.fxml.FXML
import javafx.scene.control.MenuBar
import org.apache.commons.lang3.SystemUtils

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class MainController {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        if (SystemUtils.IS_OS_MAC) {
            menuBar.isUseSystemMenuBar = true
        }
    }
}