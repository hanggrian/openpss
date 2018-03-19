package com.hendraanggrian.openpss.ui.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.ui.Addable
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.util.getResource
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.stage.Modality.APPLICATION_MODAL
import ktfx.collections.toObservableList
import ktfx.stage.stage
import java.net.URL
import java.util.ResourceBundle

class OrderController : Controller(), Refreshable, Addable {

    @FXML lateinit var statusBox: ChoiceBox<String>
    @FXML lateinit var countBox: CountBox

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refresh()

        countBox.desc = getString(R.string.items)
        statusBox.items = listOf(R.string.any, R.string.unpaid, R.string.paid).map { getString(it) }.toObservableList()
        statusBox.selectionModel.selectFirst()
    }

    override fun refresh() {
    }

    override fun add() {
        /*PlateReceiptDialog(this).showAndWait().ifPresent {

        }*/
    }

    @FXML fun platePrice() = stage(getString(R.string.plate_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_plate), resources)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun offsetPrice() = stage(getString(R.string.offset_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_offset), resources)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()
}