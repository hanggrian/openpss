package com.wijayaprinting.controllers

import com.wijayaprinting.R
import com.wijayaprinting.core.Refreshable
import com.wijayaprinting.util.controller
import com.wijayaprinting.util.pane
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.loadFXML
import kotfx.observableListOf
import kotfx.stage
import kotfx.toScene

class PlateController : Controller(), Refreshable {

    @FXML lateinit var statusBox: ChoiceBox<String>

    @FXML
    fun initialize() {
        refresh()

        statusBox.items = observableListOf(getString(R.string.any), getString(R.string.unpaid), getString(R.string.paid))
        statusBox.selectionModel.select(0)
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun clearDateOnAction() {

    }

    @FXML
    fun addOnAction() {
        /*PlateReceiptDialog(this).showAndWait().ifPresent {

        }*/
    }

    @FXML
    fun priceOnAction() = stage(getString(R.string.plate_price)) {
        val loader = getResource(R.fxml.layout_plate_price).loadFXML(resources)
        initModality(APPLICATION_MODAL)
        scene = loader.pane.toScene()
        isResizable = false
        loader.controller.employee = employee
    }.showAndWait()

    override fun refresh() {
    }
}