package com.wijayaprinting.manager.controller

import com.wijayaprinting.dao.PlateReceipt
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Refreshable
import com.wijayaprinting.manager.dialog.SearchCustomerDialog
import com.wijayaprinting.manager.scene.utils.gap
import com.wijayaprinting.manager.utils.pane
import javafx.fxml.FXML
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.*

class PlateController : Controller(), Refreshable {

    @FXML
    fun initialize() {
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun addOnAction() {
        dialog<PlateReceipt>("ASD") {
            content = gridPane {
                gap(8)
                label(getString(R.string.customer)) col 0 row 0
                button("Pick customer") {
                    setOnAction {
                        SearchCustomerDialog(this@PlateController).showAndWait()
                    }
                } col 1 row 1
            }
        }.showAndWait()
    }

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

    override fun refresh() {
    }
}