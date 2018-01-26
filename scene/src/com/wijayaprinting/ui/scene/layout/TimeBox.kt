@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.layout

import com.wijayaprinting.PATTERN_TIME
import com.wijayaprinting.ui.scene.control.IntField
import com.wijayaprinting.ui.scene.control.intField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import kotfx.*
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT
import org.joda.time.LocalTime.parse
import org.joda.time.format.DateTimeFormat.forPattern

/** Two fields (hour and minute) that represents [LocalTime]. */
open class TimeBox @JvmOverloads constructor(prefill: LocalTime = LocalTime(0, 0)) : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField

    val timeProperty: ObjectProperty<LocalTime> = SimpleObjectProperty<LocalTime>()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

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

        timeProperty bind bindingOf(hourField.valueProperty, minuteField.valueProperty) {
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

        setTimeImpl(prefill)
    }

    var time: LocalTime
        get() = timeProperty.get()
        set(value) = setTimeImpl(value)

    var isValid: Boolean
        get() = validProperty.get()
        set(value) = validProperty.set(value)

    private fun setTimeImpl(value: LocalTime) {
        hourField.text = value.hourOfDay.toString()
        minuteField.text = value.minuteOfHour.toString()
    }
}


@JvmOverloads inline fun timeBox(prefill: LocalTime = LocalTime(0, 0), noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.timeBox(prefill: LocalTime = LocalTime(0, 0), noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.timeBox(prefill: LocalTime = LocalTime(0, 0), noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }.add()
