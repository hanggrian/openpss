package com.hanggrian.openpss.control

import com.hanggrian.openpss.R
import com.hanggrian.openpss.util.toJava
import com.hanggrian.openpss.util.toJoda
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
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.KtfxHBox
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hanggrian.scene.layout.TimeBox]'s
 * width.
 */
open class DateBox(prefill: LocalDate = now()) : KtfxHBox(0.0) {
    val picker: DatePicker
    val previousButton: Button
    val nextButton: Button

    val valueProperty: ObjectProperty<LocalDate> = SimpleObjectProperty()
    val value: LocalDate? by valueProperty

    init {
        alignment = CENTER
        previousButton = styledJfxButton(null, ImageView(R.image_btn_previous), R.style_flat)
        picker =
            jfxDatePicker {
                editor.alignment = CENTER
                value = prefill.toJava()
                isEditable = false
                maxWidth = 116.0
            }
        nextButton =
            styledJfxButton(null, ImageView(R.image_btn_next), R.style_flat) {
                onAction { picker.value = picker.value.plusDays(1) }
            }

        previousButton.onAction { picker.value = picker.value.minusDays(1) }

        valueProperty.bind(bindingOf(picker.valueProperty()) { picker.value.toJoda() })
    }
}
