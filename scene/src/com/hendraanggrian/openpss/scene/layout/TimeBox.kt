@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.R
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.intField
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.ChildManager
import ktfx.layouts.ItemManager
import ktfx.layouts.LayoutDsl
import ktfx.layouts._HBox
import ktfx.layouts.button
import ktfx.layouts.label
import ktfx.scene.layout.spacings
import ktfx.scene.layout.widthMax
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.hendraanggrian.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox(prefill: LocalTime = MIDNIGHT) : _HBox() {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField
    var previousButton: Button
    var nextButton: Button
    val timeProperty: ObjectProperty<LocalTime> = SimpleObjectProperty()
    private var onOverlap: ((Boolean) -> Unit)? = null

    init {
        alignment = CENTER
        spacings = 8

        previousButton = button(graphic = ImageView(R.image.btn_arrow_left)) {
            onAction {
                hourField.value = when {
                    hourField.value == 0 -> {
                        onOverlap?.invoke(false)
                        23
                    }
                    else -> hourField.value - 1
                }
            }
        }
        hourField = intField {
            widthMax = 48
            alignment = CENTER
            valueProperty.listener { _, oldValue, value ->
                if (value !in 0 until 24) hourField.value = oldValue.toInt()
            }
        }
        label(":") { alignment = CENTER }
        minuteField = intField {
            widthMax = 48
            alignment = CENTER
            valueProperty.listener { _, oldValue, value ->
                if (value !in 0 until 60) minuteField.value = oldValue.toInt()
            }
        }
        nextButton = button(graphic = ImageView(R.image.btn_arrow_right)) {
            onAction {
                hourField.value = when {
                    hourField.value == 23 -> {
                        onOverlap?.invoke(true)
                        0
                    }
                    else -> hourField.value + 1
                }
            }
        }

        timeProperty.bind(bindingOf(hourField.valueProperty, minuteField.valueProperty) {
            LocalTime(hourField.value, minuteField.value)
        })

        hourField.text = prefill.hourOfDay.toString()
        minuteField.text = prefill.minuteOfHour.toString()
    }

    val time: LocalTime get() = timeProperty.get()

    fun setOnOverlap(action: ((plus: Boolean) -> Unit)?) {
        onOverlap = action
    }
}

inline fun timeBox(
    prefill: LocalTime = MIDNIGHT,
    noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = TimeBox(prefill).apply { init?.invoke(this) }

inline fun ChildManager.timeBox(
    prefill: LocalTime = MIDNIGHT,
    noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = com.hendraanggrian.openpss.scene.layout.timeBox(prefill, init).add()

inline fun ItemManager.timeBox(
    prefill: LocalTime = MIDNIGHT,
    noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = com.hendraanggrian.openpss.scene.layout.timeBox(prefill, init).add()