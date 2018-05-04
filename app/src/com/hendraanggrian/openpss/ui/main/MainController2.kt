package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Toggle
import javafx.scene.layout.AnchorPane
import ktfx.coroutines.listener
import org.controlsfx.control.SegmentedButton
import java.net.URL
import java.util.ResourceBundle

class MainController2 : Controller() {

    @FXML lateinit var segmentedButton: SegmentedButton
    @FXML lateinit var containerPane: AnchorPane

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        segmentedButton.toggleGroup.run {
            selectedToggleProperty().listener { _, oldValue, value -> if (value == null) selectToggle(oldValue) }
        }
    }

    @FXML fun navigate(event: ActionEvent) {
        val loader = FXMLLoader(getResource((event.source as Toggle).userData as String), resources)
        containerPane.children.let {
            it.clear()
            it += loader.pane.also {
                AnchorPane.setLeftAnchor(it, 0.0)
                AnchorPane.setTopAnchor(it, 0.0)
                AnchorPane.setRightAnchor(it, 0.0)
                AnchorPane.setBottomAnchor(it, 0.0)
            }
            loader.controller.login = login
        }
    }
}