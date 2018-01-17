package com.wijayaprinting.controllers

import com.wijayaprinting.R
import com.wijayaprinting.core.Refreshable
import com.wijayaprinting.util.controller
import com.wijayaprinting.util.pane
import javafx.fxml.FXML
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.loadFXML
import kotfx.stage
import kotfx.toScene

class PlateController : Controller(), Refreshable {

    @FXML
    fun initialize() {
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun priceOnAction() = stage(getString(R.string.plate_price)) {
        val loader = getResource(R.fxml.layout_plate_price).loadFXML(resources)
        initModality(APPLICATION_MODAL)
        scene = loader.pane.toScene()
        isResizable = false
        loader.controller.employee = employee
    }.showAndWait()

    @FXML
    fun addOnAction() {
        /*PlateReceiptDialog(this).showAndWait().ifPresent {

        }*/
    }

    override fun refresh() {
    }
}