@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.layout

import com.wijayaprinting.time.PATTERN_TIME
import com.wijayaprinting.scene.R
import com.wijayaprinting.scene.control.IntField
import com.wijayaprinting.scene.control.intField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.image.ImageView
import kotfx.annotations.LayoutDsl
import kotfx.bindings.bindingOf
import kotfx.bindings.booleanBindingOf
import kotfx.coroutines.listener
import kotfx.layout.ChildManager
import kotfx.layout.ItemManager
import kotfx.layout._HBox
import kotfx.layout.button
import kotfx.layout.label
import kotfx.maxSize
import kotfx.spacing
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT
import org.joda.time.LocalTime.parse
import org.joda.time.format.DateTimeFormat.forPattern

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.wijayaprinting.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox(prefill: LocalTime = MIDNIGHT) : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField

    val timeProperty: ObjectProperty<LocalTime> = SimpleObjectProperty<LocalTime>()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        alignment = CENTER
        spacing(8)

        button(graphic = ImageView(R.image.btn_arrow_left)) { setOnAction { hourField.value-- } }
        hourField = intField {
            maxSize(width = 48)
            alignment = CENTER
            valueProperty.listener { _, oldValue, value -> if (value !in 0 until 24) hourField.value = oldValue.toInt() }
        }
        label(":") { alignment = CENTER }
        minuteField = intField {
            maxSize(width = 48)
            alignment = CENTER
            valueProperty.listener { _, oldValue, value -> if (value !in 0 until 60) minuteField.value = oldValue.toInt() }
        }
        button(graphic = ImageView(R.image.btn_arrow_right)) { setOnAction { hourField.value++ } }

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

inline fun timeBox(prefill: LocalTime = MIDNIGHT, noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }
inline fun ChildManager.timeBox(prefill: LocalTime = MIDNIGHT, noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }.add()
inline fun ItemManager.timeBox(prefill: LocalTime = MIDNIGHT, noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }.add()
