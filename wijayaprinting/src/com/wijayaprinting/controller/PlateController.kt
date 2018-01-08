package com.wijayaprinting.controller

import com.wijayaprinting.App.Companion.EMPLOYEE
import com.wijayaprinting.R
import com.wijayaprinting.Refreshable
import com.wijayaprinting.dao.Employee
import com.wijayaprinting.dao.PlateReceipt
import com.wijayaprinting.dialog.PlateReceiptDialog
import com.wijayaprinting.dialog.SearchCustomerDialog
import com.wijayaprinting.utils.pane
import com.wijayaprinting.utils.expose
import javafx.fxml.FXML
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.loadFXML
import kotfx.stage
import kotfx.toScene
import org.joda.time.DateTime.now

class PlateController : Controller(), Refreshable {

    @FXML
    fun initialize() {
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun priceOnAction() = stage(getString(R.string.plate_price)) {
        initModality(APPLICATION_MODAL)
        scene = getResource(R.fxml.layout_plate_price).loadFXML(resources).pane.toScene()
        isResizable = false
    }.showAndWait()

    @FXML
    fun addOnAction() = SearchCustomerDialog(this).showAndWait().ifPresent { _customer ->
        val receipt = expose {
            PlateReceipt.new {
                datetime = now()
                employee = Employee.findById(EMPLOYEE)
                customer = _customer
            }
        }!!
        PlateReceiptDialog(this, receipt).showAndWait().ifPresent {

        }
    }

    override fun refresh() {
    }
}