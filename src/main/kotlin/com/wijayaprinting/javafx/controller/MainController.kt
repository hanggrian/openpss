package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.R
import javafx.fxml.FXML
import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class MainController {

    @FXML lateinit var customerTab: Tab
    @FXML lateinit var attendanceTab: Tab
    @FXML lateinit var attendanceController: AttendanceController
    @FXML lateinit var aboutTab: Tab

    @FXML
    fun initialize() {

        customerTab.graphic = ImageView(Image(R.png.tab_customer))
        attendanceTab.graphic = ImageView(Image(R.png.tab_attendance))
        aboutTab.graphic = ImageView(Image(R.png.tab_about))
    }
}