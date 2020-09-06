@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.toJava
import com.hendraanggrian.openpss.util.toJoda
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import ktfx.bindings.bindingOf
import ktfx.controls.CENTER
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxDatePicker
import ktfx.jfoenix.layouts.styledJFXButton
import ktfx.layouts.KtfxHBox
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hendraanggrian.scene.layout.TimeBox]'s width.
 */
open class DateBox @JvmOverloads constructor(prefill: LocalDate = now()) : KtfxHBox(0.0) {

    lateinit var picker: DatePicker
    var previousButton: Button
    var nextButton: Button

    private val valueProperty = SimpleObjectProperty<LocalDate>()
    fun valueProperty(): ObjectProperty<LocalDate> = valueProperty
    val value: LocalDate? by valueProperty

    init {
        alignment = CENTER
        previousButton = styledJFXButton(null, ImageView(R.image.btn_previous), R.style.flat) {
            onAction { picker.value = picker.value.minusDays(1) }
        }
        picker = jfxDatePicker {
            editor.alignment = CENTER
            value = prefill.toJava()
            isEditable = false
            maxWidth = 116.0
        }
        nextButton = styledJFXButton(null, ImageView(R.image.btn_next), R.style.flat) {
            onAction { picker.value = picker.value.plusDays(1) }
        }
        valueProperty.bind(bindingOf(picker.valueProperty()) { picker.value.toJoda() })
    }
}
