package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.App
import com.wijayaprinting.manager.R
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.*

class PlateController : Controller() {

    @FXML lateinit var priceButton: Button

    @FXML
    fun initialize() {
        priceButton.disableProperty() bind not(App.fullAccessProperty)
    }

    @FXML
    fun priceOnAction() {
        val minSize = Pair(360.0, 640.0)
        stage("${getString(R.string.app_name)} - ${getString(R.string.plate_price)}") {
            initModality(APPLICATION_MODAL)
            val loader = getResource(R.fxml.layout_plate_price).loadFXML(resources)
            scene = loader.load<Pane>().toScene(minSize.first, minSize.second)
            minWidth = minSize.first
            minHeight = minSize.second
        }.showAndWait()
    }
}