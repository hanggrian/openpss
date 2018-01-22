package com.wijayaprinting.ui

import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane

/** Load the pane without generic. */
inline val FXMLLoader.pane: Pane get() = load<Pane>()

/** Returns default controller, must be called after loading the pane. */
inline val FXMLLoader.controller: Controller get() = getController<Controller>()