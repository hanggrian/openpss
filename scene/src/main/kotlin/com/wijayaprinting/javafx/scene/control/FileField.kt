package com.wijayaprinting.javafx.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.or
import java.io.File

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class FileField : TextField() {

    val validProperty = SimpleBooleanProperty().apply {
        bind(textProperty().isEmpty or booleanBindingOf(textProperty()) {
            val file = File(text)
            !file.exists() || !file.isFile
        })
    }
    val isValid get() = validProperty.value
}