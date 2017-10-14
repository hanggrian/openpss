package com.wijayaprinting.javafx.controller

import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control.MenuBar
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotfx.runLater
import org.apache.commons.lang3.SystemUtils

class MainController {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        if (SystemUtils.IS_OS_MAC) {
            menuBar.isUseSystemMenuBar = true
        }
        runLater {
            val region = tabPane.lookup(".headers-region") as StackPane
            val regionTop = tabPane.lookup(".tab-pane:top *.tab-header-area") as StackPane
            regionTop.widthProperty().addListener { _, _, arg2 ->
                val insets = regionTop.padding
                regionTop.padding = Insets(
                        insets.top,
                        insets.right,
                        insets.bottom,
                        arg2.toDouble() / 2 - region.width / 2)
            }
            // force re-layout so the tabs aligned to center at initial state
            (tabPane.scene.window as Stage).let { stage -> stage.width = stage.width + 1 }
        }
    }
}