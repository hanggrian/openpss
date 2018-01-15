@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.layouts

import com.wijayaprinting.PATTERN_TIME
import com.wijayaprinting.controls.IntField
import com.wijayaprinting.controls.intField
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import kotfx.*
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT
import org.joda.time.LocalTime.parse
import org.joda.time.format.DateTimeFormat.forPattern

open class TimeBox : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField

    val valueProperty = SimpleObjectProperty<LocalTime>()
    val validProperty = SimpleBooleanProperty()

    init {
        alignment = CENTER
        spacing = 4.0

        hourField = intField {
            maxWidth = 48.0
            alignment = CENTER
            textProperty().addListener { _, oldValue, newValue -> if (newValue.toIntOrNull() ?: 0 !in 0 until 24) hourField.text = oldValue }
        }
        label(":")
        minuteField = intField {
            maxWidth = 48.0
            alignment = CENTER
            textProperty().addListener { _, oldValue, newValue -> if (newValue.toIntOrNull() ?: 0 !in 0 until 60) minuteField.text = oldValue }
        }

        valueProperty bind bindingOf(hourField.valueProperty, minuteField.valueProperty) {
            try {
                parse("${hourField.value}:${minuteField.value}", forPattern(PATTERN_TIME))
            } catch (e: Exception) {
                MIDNIGHT
            }
        }
        validProperty bind booleanBindingOf(hourField.valueProperty, minuteField.valueProperty) {
            try {
                parse("${hourField.value}:${minuteField.value}", forPattern(PATTERN_TIME))
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

    var isValid: Boolean
        get() = validProperty.get()
        set(value) = validProperty.set(value)
}


@JvmOverloads inline fun timeBox(noinline init: ((@KotfxDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.timeBox(noinline init: ((@KotfxDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.timeBox(noinline init: ((@KotfxDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox().apply { init?.invoke(this) }.add()