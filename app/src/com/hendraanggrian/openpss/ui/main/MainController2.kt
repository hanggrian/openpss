package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import com.hendraanggrian.openpss.util.segmentedController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Toggle
import javafx.scene.control.ToolBar
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region
import ktfx.application.later
import ktfx.coroutines.listener
import org.controlsfx.control.SegmentedButton
import java.net.URL
import java.util.ResourceBundle

class MainController2 : Controller() {

    @FXML lateinit var navigationBar: ToolBar
    @FXML lateinit var leftRegion: Region
    @FXML lateinit var segmentedButton: SegmentedButton
    @FXML lateinit var rightRegion: Region
    @FXML lateinit var containerPane: AnchorPane

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        segmentedButton.run {
            toggleGroup.selectedToggleProperty().listener { _, oldValue, value ->
                if (value == null) toggleGroup.selectToggle(oldValue)
            }
            later { buttons.first().fire() }
        }
    }

    @FXML fun navigate(event: ActionEvent) {
        val loader = FXMLLoader(getResource((event.source as Toggle).userData as String), resources)
        val pane = loader.pane
        val controller = loader.segmentedController
        containerPane.children.let {
            it.clear()
            it += pane.also {
                AnchorPane.setLeftAnchor(it, 0.0)
                AnchorPane.setTopAnchor(it, 0.0)
                AnchorPane.setRightAnchor(it, 0.0)
                AnchorPane.setBottomAnchor(it, 0.0)
            }
        }
        controller.login = login
        navigationBar.items.run {
            removeAll(filter { it != segmentedButton && it != leftRegion && it != rightRegion })
            controller.leftSegment.reversed().forEach { add(0, it) }
            controller.rightSegment.forEach { add(size, it) }
        }
    }
}