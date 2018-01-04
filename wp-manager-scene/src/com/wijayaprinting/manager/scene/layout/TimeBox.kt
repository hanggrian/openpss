@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.layout

import com.wijayaprinting.PATTERN_TIME
import com.wijayaprinting.manager.scene.control.IntField
import com.wijayaprinting.manager.scene.control.intField
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import kotfx.*
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

open class TimeBox : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField

    val valueProperty = SimpleObjectProperty<LocalTime>()
    val validProperty = SimpleBooleanProperty()

    init {
        alignment = CENTER
        spacing = 4.0

        hourField = intField {
            promptText = "0"
            maxWidth = 48.0
            alignment = CENTER
            textProperty().addListener { _, oldValue, newValue -> if (newValue.toIntOrNull() ?: 0 !in 0..24) hourField.text = oldValue }
        }
        label(":")
        minuteField = intField {
            promptText = "0"
            maxWidth = 48.0
            alignment = CENTER
            textProperty().addListener { _, oldValue, newValue -> if (newValue.toIntOrNull() ?: 0 !in 0..60) minuteField.text = oldValue }
        }

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


@JvmOverloads inline fun timeBox(noinline init: ((@KotfxDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.timeBox(noinline init: ((@KotfxDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.timeBox(noinline init: ((@KotfxDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox().apply { init?.invoke(this) }.add()