package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.BuildConfig
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.utils.getString
import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.MenuBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import kotfx.dialogs.infoAlert
import org.apache.commons.lang3.SystemUtils
import java.awt.Desktop
import java.net.URI

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class MainController {

    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var attendanceController: AttendanceController

    @FXML
    fun initialize() {
        if (SystemUtils.IS_OS_MAC) {
            menuBar.isUseSystemMenuBar = true
        }
    }

    @FXML
    fun aboutMenuItemOnAction() = infoAlert(ImageView(Image(R.png.ic_launcher)), "${getString(R.string.app_name)} ${BuildConfig.VERSION}", getString(R.string.about_content)) {
        expandableContent = VBox(
                Label(getString(R.string.about_expandable_content)),
                Hyperlink("https://github.com/WijayaPrinting/wp-attendance").apply { setOnAction { Desktop.getDesktop().browse(URI(text)) } })
        isExpanded = true
    }.showAndWait()!!
}