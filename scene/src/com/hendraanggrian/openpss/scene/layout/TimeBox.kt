@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.R
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.time.PATTERN_TIME
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.image.ImageView
import kotfx.beans.binding.bindingOf
import kotfx.beans.binding.booleanBindingOf
import kotfx.coroutines.listener
import kotfx.coroutines.onAction
import kotfx.layouts.ChildManager
import kotfx.layouts.ItemManager
import kotfx.layouts.LayoutDsl
import kotfx.layouts._HBox
import kotfx.layouts.button
import kotfx.layouts.label
import kotfx.scene.layout.maxSize
import kotfx.scene.layout.spacings
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT
import org.joda.time.LocalTime.parse
import org.joda.time.format.DateTimeFormat.forPattern

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.hendraanggrian.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox(prefill: LocalTime = MIDNIGHT) : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField

    val timeProperty: ObjectProperty<LocalTime> = SimpleObjectProperty<LocalTime>()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        alignment = CENTER
        spacings = 8

        button(graphic = ImageView(R.image.btn_arrow_left)) { onAction { hourField.value-- } }
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
        button(graphic = ImageView(R.image.btn_arrow_right)) { onAction { hourField.value++ } }

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
