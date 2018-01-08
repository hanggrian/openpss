package com.wijayaprinting.utils

import com.wijayaprinting.controller.Controller
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane

inline val FXMLLoader.pane: Pane get() = load<Pane>()

inline val FXMLLoader.controller: Controller get() = getController<Controller>()