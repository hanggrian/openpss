package com.wijayaprinting.manager.scene.layout

import com.wijayaprinting.data.PATTERN_TIME
import com.wijayaprinting.manager.scene.control.IntField
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import kotfx.bind
import kotfx.bindings.bindingOf
import kotfx.bindings.booleanBindingOf
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

open class TimeBox : HBox() {

    val hourField = IntField()
    val dividerLabel = Label(":")
    val minuteField = IntField()

    val valueProperty = SimpleObjectProperty<LocalTime>()
    val validProperty = SimpleBooleanProperty()

    init {
        alignment = CENTER
        spacing = 4.0

        hourField.textProperty().addListener { _, oldValue, newValue -> if (newValue.toIntOrNull() ?: 0 !in 0..24) hourField.text = oldValue }
        minuteField.textProperty().addListener { _, oldValue, newValue -> if (newValue.toIntOrNull() ?: 0 !in 0..60) minuteField.text = oldValue }
        listOf(hourField, minuteField).forEach {
            it.promptText = "00"
            it.maxWidth = 48.0
            it.alignment = CENTER
        }

        children.addAll(hourField, dividerLabel, minuteField)

        valueProperty bind bindingOf(hourField.textProperty(), minuteField.textProperty()) {
            try {
                LocalTime.parse("${hourField.text}:${minuteField.text}", DateTimeFormat.forPattern(PATTERN_TIME))
            } catch (e: Exception) {
                LocalTime.MIDNIGHT
            }
        }
        validProperty bind booleanBindingOf(hourField.textProperty(), minuteField.textProperty()) {
            try {
                LocalTime.parse("${hourField.text}:${minuteField.text}", DateTimeFormat.forPattern(PATTERN_TIME))
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    var value: LocalTime
        get() = valueProperty.get()
        set(value) {
            hourField.text = value.hourOfDay.toString()
            minuteField.text = value.minuteOfHour.toString()
        }

    val isValid: Boolean = validProperty.get()
}