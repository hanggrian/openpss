@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.layouts

import com.hendraanggrian.openpss.controls.IntField
import com.hendraanggrian.openpss.controls.intField
import com.hendraanggrian.openpss.scene.R
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.getValue
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._HBox
import ktfx.layouts.button
import ktfx.layouts.label
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.hendraanggrian.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox @JvmOverloads constructor(prefill: LocalTime = MIDNIGHT) : _HBox(8.0) {

    lateinit var hourField: IntField
    lateinit var minuteField: IntField
    var previousButton: Button
    var nextButton: Button
    private var onOverlap: ((Boolean) -> Unit)? = null

    val valueProperty: ObjectProperty<LocalTime> = SimpleObjectProperty()
    val value: LocalTime by valueProperty

    init {
        alignment = CENTER

        previousButton = button(graphic = ImageView(R.image.btn_previous)) {
            onAction {
                hourField.value = when (hourField.value) {
                    0 -> {
                        onOverlap?.invoke(false)
                        23
                    }
                    else -> hourField.value - 1
                }
            }
        }
        hourField = intField {
            maxWidth = 48.0
            alignment = CENTER
            valueProperty.listener { _, oldValue, value ->
                if (value !in 0 until 24) hourField.value = oldValue.toInt()
            }
        }
        label(":") { alignment = CENTER }
        minuteField = intField {
            maxWidth = 48.0
            alignment = CENTER
            valueProperty.listener { _, oldValue, value ->
                if (value !in 0 until 60) minuteField.value = oldValue.toInt()
            }
        }
        nextButton = button(graphic = ImageView(R.image.btn_next)) {
            onAction {
                hourField.value = when (hourField.value) {
                    23 -> {
                        onOverlap?.invoke(true)
                        0
                    }
                    else -> hourField.value + 1
                }
            }
        }

        valueProperty.bind(bindingOf(hourField.valueProperty, minuteField.valueProperty) {
            LocalTime(hourField.value, minuteField.value)
        })

        hourField.text = prefill.hourOfDay.toString()
        minuteField.text = prefill.minuteOfHour.toString()
    }

    fun setOnOverlap(action: ((plus: Boolean) -> Unit)?) {
        onOverlap = action
    }
}

/** Creates a [TimeBox]. */
fun timeBox(
    prefill: LocalTime = MIDNIGHT,
    init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = TimeBox(prefill).also {
    init?.invoke(it)
}

/** Creates a [TimeBox] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.timeBox(
    prefill: LocalTime = MIDNIGHT,
    noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = com.hendraanggrian.openpss.layouts.timeBox(prefill, init).add()