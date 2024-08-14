package com.hanggrian.openpss.util

import com.hanggrian.openpss.ui.Controller
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Region

/** Load the pane without generic. */
inline val FXMLLoader.pane: Region get() = load()

/** Returns default controller, must be called after loading the pane. */
inline val FXMLLoader.controller: Controller get() = getController()
