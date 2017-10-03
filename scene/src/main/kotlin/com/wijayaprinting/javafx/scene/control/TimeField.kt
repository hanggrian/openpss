package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.mysql.utils.PATTERN_TIME
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import kotfx.bindings.bindingOf
import kotfx.bindings.booleanBindingOf
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class TimeField : TextField() {

    val valueProperty = SimpleObjectProperty<LocalTime>().apply {
        bind(bindingOf(textProperty()) {
            try {
                LocalTime.parse(text, DateTimeFormat.forPattern(PATTERN_TIME))
            } catch (e: Exception) {
                null
            }
        })
    }
    var value: LocalTime?
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val validProperty = SimpleBooleanProperty().apply {
        bind(booleanBindingOf(textProperty()) {
            try {
                LocalTime.parse(text, DateTimeFormat.forPattern(PATTERN_TIME))
                true
            } catch (e: Exception) {
                false
            }
        })
    }
    val isValid: Boolean = validProperty.value

    init {
        promptText = PATTERN_TIME
    }
}