package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.toJava
import com.hendraanggrian.openpss.util.toJoda
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxDatePicker
import ktfx.layouts.KtfxHBox
import ktfx.toAny
import org.joda.time.LocalDate

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hendraanggrian.scene.layout.TimeBox]'s width.
 */
open class DateBox @JvmOverloads constructor(prefill: LocalDate = LocalDate.now()) : KtfxHBox(0.0) {

    lateinit var picker: DatePicker
    var previousButton: Button
    var nextButton: Button

    private val valueProperty = SimpleObjectProperty<LocalDate>()
    fun valueProperty(): ObjectProperty<LocalDate> = valueProperty
    val value: LocalDate? by valueProperty

    init {
        alignment = Pos.CENTER
        previousButton = jfxButton(graphic = ImageView(R.image.btn_previous)) {
            styleClass += R.style.flat
            onAction { picker.value = picker.value.minusDays(1) }
        }
        picker = jfxDatePicker {
            editor.alignment = Pos.CENTER
            value = prefill.toJava()
            isEditable = false
            maxWidth = 116.0
        }
        nextButton = jfxButton(graphic = ImageView(R.image.btn_next)) {
            styleClass += R.style.flat
            onAction { picker.value = picker.value.plusDays(1) }
        }
        valueProperty.bind(picker.valueProperty().toAny { it!!.toJoda() })
    }
}
