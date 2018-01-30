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

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.wijayaprinting.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox @JvmOverloads constructor(prefill: LocalTime = MIDNIGHT) : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField

    val timeProperty: ObjectProperty<LocalTime> = SimpleObjectProperty<LocalTime>()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        alignment = CENTER

        hourField = intField {
            maxWidth = 52.0
            alignment = CENTER
            valueProperty.addListener { _, oldValue, newValue -> if (newValue !in 0 until 24) hourField.value = oldValue.toInt() }
        }
        label(":") {
            minWidth = 12.0
            alignment = CENTER
        }
        minuteField = intField {
            maxWidth = 52.0
            alignment = CENTER
            valueProperty.addListener { _, oldValue, newValue -> if (newValue !in 0 until 60) minuteField.value = oldValue.toInt() }
        }

        timeProperty.bind(bindingOf(hourField.valueProperty, minuteField.valueProperty) {
            try {
                parse("${hourField.value}:${minuteField.value}", forPattern(PATTERN_TIME))
            } catch (e: Exception) {
                MIDNIGHT
            }
        })
        validProperty.bind(booleanBindingOf(hourField.valueProperty, minuteField.valueProperty) {
            try {
                parse("${hourField.value}:${minuteField.value}", forPattern(PATTERN_TIME))
                true
            } catch (e: Exception) {
                false
            }
        })

        hourField.text = prefill.hourOfDay.toString()
        minuteField.text = prefill.minuteOfHour.toString()
    }

    val time: LocalTime get() = timeProperty.get()

    val isValid: Boolean get() = validProperty.get()
}


@JvmOverloads inline fun timeBox(prefill: LocalTime = MIDNIGHT, noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.timeBox(prefill: LocalTime = MIDNIGHT, noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.timeBox(prefill: LocalTime = MIDNIGHT, noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }.add()
