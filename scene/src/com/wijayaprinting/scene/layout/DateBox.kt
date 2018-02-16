@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.layout

import com.wijayaprinting.scene.R
import com.wijayaprinting.scene.toJava
import javafx.geometry.Pos.CENTER
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import kotfx.annotations.SceneDsl
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
import kotfx.scene._HBox
import kotfx.scene.button
import kotfx.scene.datePicker
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.wijayaprinting.scene.layout.TimeBox]'s width.
 */
open class DateBox(prefill: LocalDate = now()) : _HBox() {

    lateinit var picker: DatePicker

    init {
        alignment = CENTER
        spacing = 8.0

        button(graphic = ImageView(R.image.btn_arrow_left)) { setOnAction { picker.value = picker.value.minusDays(1) } }
        picker = datePicker {
            value = prefill.toJava()
            isEditable = false
            maxWidth = 116.0
        }
        button(graphic = ImageView(R.image.btn_arrow_right)) { setOnAction { picker.value = picker.value.plusDays(1) } }
    }
}

inline fun dateBox(prefill: LocalDate = now(), noinline init: ((@SceneDsl DateBox).() -> Unit)? = null): DateBox = DateBox(prefill).apply { init?.invoke(this) }
inline fun ChildManager.dateBox(prefill: LocalDate = now(), noinline init: ((@SceneDsl DateBox).() -> Unit)? = null): DateBox = DateBox(prefill).apply { init?.invoke(this) }.add()
inline fun ItemManager.dateBox(prefill: LocalDate = now(), noinline init: ((@SceneDsl DateBox).() -> Unit)? = null): DateBox = DateBox(prefill).apply { init?.invoke(this) }.add()
