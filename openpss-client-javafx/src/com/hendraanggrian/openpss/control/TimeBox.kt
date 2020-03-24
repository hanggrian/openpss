package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.toJava
import com.hendraanggrian.openpss.util.toJoda
import com.jfoenix.controls.JFXTimePicker
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxTimePicker
import ktfx.layouts.KtfxHBox
import ktfx.text.buildStringConverter
import ktfx.toBinding
import org.joda.time.LocalTime

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.hendraanggrian.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox @JvmOverloads constructor(prefill: LocalTime = LocalTime.MIDNIGHT) : KtfxHBox(0.0) {

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
            styleClass += R.style.flat
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
            editor.alignment = Pos.CENTER
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
            styleClass += R.style.flat
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

        valueProperty.bind(picker.valueProperty().toBinding { it!!.toJoda() })
    }
}
