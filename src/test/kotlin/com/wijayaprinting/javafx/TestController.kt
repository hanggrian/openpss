package com.wijayaprinting.javafx

import javafx.fxml.FXML
import javafx.scene.control.Label
import kotfx.runLater

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class TestController : Controller<String>() {

    @FXML lateinit var label: Label

    @FXML
    fun initialize() = runLater {
        label.text = extra
    }
}