package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.SegmentedController
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Region

/** Load the pane without generic. */
inline val FXMLLoader.pane: Region get() = load<Region>()

/** Returns default controller, must be called after loading the pane. */
inline val FXMLLoader.controller: Controller get() = getController<Controller>()

/** Returns default controller, must be called after loading the pane. */
inline val FXMLLoader.segmentedController: SegmentedController get() = getController<SegmentedController>()