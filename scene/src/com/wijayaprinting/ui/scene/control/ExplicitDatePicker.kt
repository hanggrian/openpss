@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import com.wijayaprinting.asJava
import javafx.scene.control.DatePicker
import kotfx.ChildRoot
import kotfx.ItemRoot
import kotfx.LayoutDsl
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/** A [DatePicker] that always has a valid value. */
open class ExplicitDatePicker @JvmOverloads constructor(prefill: LocalDate = now()) : DatePicker() {

    init {
        value = prefill.asJava()
        isEditable = false
        maxWidth = 128.0
    }
}

@JvmOverloads inline fun explicitDatePicker(prefill: LocalDate = now(), noinline init: ((@LayoutDsl ExplicitDatePicker).() -> Unit)? = null): ExplicitDatePicker = ExplicitDatePicker(prefill).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.explicitDatePicker(prefill: LocalDate = now(), noinline init: ((@LayoutDsl ExplicitDatePicker).() -> Unit)? = null): ExplicitDatePicker = ExplicitDatePicker(prefill).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.explicitDatePicker(prefill: LocalDate = now(), noinline init: ((@LayoutDsl ExplicitDatePicker).() -> Unit)? = null): ExplicitDatePicker = ExplicitDatePicker(prefill).apply { init?.invoke(this) }.add()
