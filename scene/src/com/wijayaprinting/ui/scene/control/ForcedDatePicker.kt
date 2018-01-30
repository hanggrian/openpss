@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import com.wijayaprinting.toJava
import javafx.scene.control.DatePicker
import kotfx.ChildRoot
import kotfx.ItemRoot
import kotfx.LayoutDsl
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [ForcedDatePicker] width is deliberately measured to match [com.wijayaprinting.ui.scene.layout.TimeBox]'s width.
 */
open class ForcedDatePicker @JvmOverloads constructor(prefill: LocalDate = now()) : DatePicker() {

    init {
        value = prefill.toJava()
        isEditable = false
        maxWidth = 116.0
    }
}

@JvmOverloads inline fun forcedDatePicker(prefill: LocalDate = now(), noinline init: ((@LayoutDsl ForcedDatePicker).() -> Unit)? = null): ForcedDatePicker = ForcedDatePicker(prefill).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.forcedDatePicker(prefill: LocalDate = now(), noinline init: ((@LayoutDsl ForcedDatePicker).() -> Unit)? = null): ForcedDatePicker = ForcedDatePicker(prefill).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.forcedDatePicker(prefill: LocalDate = now(), noinline init: ((@LayoutDsl ForcedDatePicker).() -> Unit)? = null): ForcedDatePicker = ForcedDatePicker(prefill).apply { init?.invoke(this) }.add()
