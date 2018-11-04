@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.toJava
import com.hendraanggrian.openpss.content.toJoda
import com.jfoenix.controls.JFXTimePicker
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import ktfx.LayoutDsl
import ktfx.NodeInvokable
import ktfx.beans.binding.buildBinding
import ktfx.beans.value.getValue
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxTimePicker
import ktfx.layouts._HBox
import ktfx.listeners.buildStringConverter
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.hendraanggrian.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox @JvmOverloads constructor(prefill: LocalTime = MIDNIGHT) : _HBox(0.0) {

    lateinit var picker: JFXTimePicker
    var previousButton: Button
    var nextButton: Button
    var onOverlap: ((Boolean) -> Unit)? = null

    private val valueProperty = SimpleObjectProperty<LocalTime>()
    fun valueProperty(): ObjectProperty<LocalTime> = valueProperty
    val value: LocalTime? by valueProperty

    init {
        alignment = Pos.CENTER
        previousButton = jfxButton(graphic = ImageView(R.image.btn_previous)) {
            styleClass += App.STYLE_BUTTON_FLAT
            onAction {
                picker.value = when (picker.value.hour) {
                    0 -> {
                        onOverlap?.invoke(false)
                        java.time.LocalTime.of(23, picker.value.minute, picker.value.second)
                    }
                    else -> picker.value.minusHours(1)
                }
            }
        }
        picker = jfxTimePicker {
            editor.run {
                alignment = CENTER
                onMouseClicked { picker.show() }
            }
            setIs24HourView(true)
            value = prefill.toJava()
            isEditable = false
            maxWidth = 116.0
            converter = buildStringConverter {
                fromString {
                    val a = it.split(':')
                    java.time.LocalTime.of(a[0].toInt(), a[1].toInt(), 0)
                }
                toString {
                    val s = it.toString()
                    when {
                        s.split(':').size > 2 -> s.substringBeforeLast(':')
                        else -> s
                    }
                }
            }
        }
        nextButton = jfxButton(graphic = ImageView(R.image.btn_next)) {
            styleClass += App.STYLE_BUTTON_FLAT
            onAction {
                picker.value = when (picker.value.hour) {
                    23 -> {
                        onOverlap?.invoke(true)
                        java.time.LocalTime.of(0, picker.value.minute, picker.value.second)
                    }
                    else -> picker.value.plusHours(1)
                }
            }
        }

        valueProperty.bind(buildBinding(picker.valueProperty()) { picker.value.toJoda() })
    }
}

/** Creates a [TimeBox]. */
fun timeBox(
    prefill: LocalTime = MIDNIGHT,
    init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = TimeBox(prefill).also {
    init?.invoke(it)
}

/** Creates a [TimeBox] and add it to this manager. */
inline fun NodeInvokable.timeBox(
    prefill: LocalTime = MIDNIGHT,
    noinline init: ((@LayoutDsl TimeBox).() -> Unit)? = null
): TimeBox = (com.hendraanggrian.openpss.control.timeBox(prefill, init))()