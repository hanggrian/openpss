@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.toJava
import com.hendraanggrian.openpss.util.toJoda
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.getValue
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._HBox
import ktfx.layouts.button
import ktfx.layouts.datePicker
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hendraanggrian.scene.layout.TimeBox]'s width.
 */
open class DateBox @JvmOverloads constructor(prefill: LocalDate = now()) : _HBox(0.0) {

    lateinit var picker: DatePicker
    var previousButton: Button
    var nextButton: Button

    private val valueProperty = SimpleObjectProperty<LocalDate>()
    fun valueProperty(): ObjectProperty<LocalDate> = valueProperty
    val value: LocalDate by valueProperty

    init {
        previousButton = button(graphic = ImageView(R.image.btn_previous)) {
            onAction { picker.value = picker.value.minusDays(1) }
        }
        picker = datePicker {
            editor.alignment = CENTER
            value = prefill.toJava()
            isEditable = false
            maxWidth = 116.0
        }
        nextButton = button(graphic = ImageView(R.image.btn_next)) {
            onAction { picker.value = picker.value.plusDays(1) }
        }

        valueProperty.bind(bindingOf(picker.valueProperty()) { picker.value.toJoda() })
    }
}

/** Creates a [DateBox]. */
fun dateBox(
    prefill: LocalDate = now(),
    init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = DateBox(prefill).also {
    init?.invoke(it)
}

/** Creates a [DateBox] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.dateBox(
    prefill: LocalDate = now(),
    noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = (com.hendraanggrian.openpss.control.dateBox(prefill, init))()