package com.wijayaprinting.controllers

import com.wijayaprinting.R
import com.wijayaprinting.base.Refreshable
import com.wijayaprinting.util.controller
import com.wijayaprinting.util.pane
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.stage.Modality
import kotfx.loadFXML
import kotfx.observableListOf
import kotfx.stage
import kotfx.toScene

class OrderController : Controller(), Refreshable {

    @FXML lateinit var statusBox: ChoiceBox<String>

    @FXML
    override fun initialize() {
        onRefresh()

        statusBox.items = observableListOf(getString(R.string.any), getString(R.string.unpaid), getString(R.string.paid))
        statusBox.selectionModel.select(0)
    }

    @FXML
    override fun onRefresh() {

    }

    @FXML
    fun onClearDate() {

    }

    @FXML
    fun onAdd() {
        /*PlateReceiptDialog(this).showAndWait().ifPresent {

        }*/
    }

    @FXML
    fun onPlatePrice() = stage(getString(R.string.plate_price)) {
        val loader = getResource(R.fxml.layout_price_plate).loadFXML(resources)
        initModality(Modality.APPLICATION_MODAL)
        scene = loader.pane.toScene()
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML
    fun onOffsetPrice() = stage(getString(R.string.offset_price)) {
        val loader = getResource(R.fxml.layout_price_offset).loadFXML(resources)
        initModality(Modality.APPLICATION_MODAL)
        scene = loader.pane.toScene()
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()
}