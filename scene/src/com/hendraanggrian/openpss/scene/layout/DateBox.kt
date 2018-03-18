@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.R
import com.hendraanggrian.openpss.time.toJava
import com.hendraanggrian.openpss.time.toJoda
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.coroutines.onAction
import ktfx.layouts.ChildManager
import ktfx.layouts.ItemManager
import ktfx.layouts.LayoutDsl
import ktfx.layouts._HBox
import ktfx.layouts.button
import ktfx.layouts.datePicker
import ktfx.scene.layout.spacings
import ktfx.scene.layout.widthMax
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hendraanggrian.scene.layout.TimeBox]'s width.
 */
open class DateBox(prefill: LocalDate = now()) : _HBox() {

    lateinit var picker: DatePicker
    var previousButton: Button
    var nextButton: Button

    val dateProperty: ObjectProperty<LocalDate> = SimpleObjectProperty()

    init {
        alignment = CENTER
        spacings = 8

        previousButton = button(graphic = ImageView(R.image.btn_arrow_left)) {
            onAction { picker.value = picker.value.minusDays(1) }
        }
        picker = datePicker {
            value = prefill.toJava()
            isEditable = false
            widthMax = 116
        }
        dateProperty.bind(bindingOf(picker.valueProperty()) { picker.value.toJoda() })
        nextButton = button(graphic = ImageView(R.image.btn_arrow_right)) {
            onAction { picker.value = picker.value.plusDays(1) }
        }
    }
}

inline fun dateBox(
    prefill: LocalDate = now(),
    noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = DateBox(prefill).apply { init?.invoke(this) }

inline fun ChildManager.dateBox(
    prefill: LocalDate = now(),
    noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = com.hendraanggrian.openpss.scene.layout.dateBox(prefill, init).add()

inline fun ItemManager.dateBox(
    prefill: LocalDate = now(),
    noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = com.hendraanggrian.openpss.scene.layout.dateBox(prefill, init).add()