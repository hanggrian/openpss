package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Refreshable
import com.wijayaprinting.manager.dialog.PlateReceiptDialog
import com.wijayaprinting.manager.utils.pane
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
    fun priceOnAction() {
        val minSize = Pair(240.0, 480.0)
        stage(getString(R.string.plate_price)) {
            initModality(APPLICATION_MODAL)
            scene = getResource(R.fxml.layout_plate_price).loadFXML(resources).pane.toScene(minSize.first, minSize.second)
            minWidth = minSize.first
            minHeight = minSize.second
            isResizable = false
        }.showAndWait()
    }

    @FXML
    fun addOnAction() = PlateReceiptDialog(this).showAndWait().ifPresent {

    }

    override fun refresh() {
    }
}