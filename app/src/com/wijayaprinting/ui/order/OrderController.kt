package com.wijayaprinting.ui.order

import com.wijayaprinting.R
import com.wijayaprinting.ui.Controller
import com.wijayaprinting.ui.Refreshable
import com.wijayaprinting.ui.controller
import com.wijayaprinting.ui.pane
import com.wijayaprinting.ui.scene.control.CountBox
import com.wijayaprinting.util.getResource
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.collections.observableListOf
import kotfx.scene.loadFXML
import kotfx.scene.toScene
import kotfx.stage

class OrderController : Controller(), Refreshable {

    @FXML lateinit var statusBox: ChoiceBox<String>
    @FXML lateinit var countBox: CountBox

    override fun initialize() {
        refresh()

        countBox.desc = getString(R.string.items)
        statusBox.items = observableListOf(getString(R.string.any), getString(R.string.unpaid), getString(R.string.paid))
        statusBox.selectionModel.select(0)
    }

    override fun refresh() {

    }

    @FXML
    fun add() {
        /*PlateReceiptDialog(this).showAndWait().ifPresent {

        }*/
    }

    @FXML
    fun platePrice() = stage(getString(R.string.plate_price)) {
        initModality(APPLICATION_MODAL)
        val loader = getResource(R.layout.controller_price_plate).loadFXML(resources)
        scene = loader.pane.toScene()
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML
    fun offsetPrice() = stage(getString(R.string.offset_price)) {
        initModality(APPLICATION_MODAL)
        val loader = getResource(R.layout.controller_price_offset).loadFXML(resources)
        scene = loader.pane.toScene()
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()
}