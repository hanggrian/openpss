package com.wijayaprinting.javafx.control.field

import com.wijayaprinting.mysql.utils.PATTERN_TIME
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import kotfx.bindings.bindingOf
import kotfx.bindings.booleanBindingOf
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class TimeField : TextField(PATTERN_TIME) {

    val valueProperty: SimpleObjectProperty<LocalTime> = SimpleObjectProperty<LocalTime>().apply {
        bind(bindingOf(textProperty()) {
            try {
                LocalTime.parse(text, DateTimeFormat.forPattern(PATTERN_TIME))
            } catch (e: Exception) {
                null
            }
        })
    }
    val value: LocalTime? get() = valueProperty.value

    val validProperty: SimpleBooleanProperty = SimpleBooleanProperty().apply {
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
}