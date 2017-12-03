package com.wijayaprinting.manager.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.bind
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.or
import java.io.File

open class FileField : TextField() {

    val validProperty = SimpleBooleanProperty()

    init {
        validProperty bind (textProperty().isEmpty or booleanBindingOf(textProperty()) {
            val file = File(text)
            !file.exists() || !file.isFile
        })
    }

    val isValid: Boolean get() = validProperty.value
}