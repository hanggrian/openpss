@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import com.wijayaprinting.toJava
import javafx.scene.control.DatePicker
import kotfx.annotations.SceneDsl
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
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

@JvmOverloads inline fun forcedDatePicker(prefill: LocalDate = now(), noinline init: ((@SceneDsl ForcedDatePicker).() -> Unit)? = null): ForcedDatePicker = ForcedDatePicker(prefill).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildManager.forcedDatePicker(prefill: LocalDate = now(), noinline init: ((@SceneDsl ForcedDatePicker).() -> Unit)? = null): ForcedDatePicker = ForcedDatePicker(prefill).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemManager.forcedDatePicker(prefill: LocalDate = now(), noinline init: ((@SceneDsl ForcedDatePicker).() -> Unit)? = null): ForcedDatePicker = ForcedDatePicker(prefill).apply { init?.invoke(this) }.add()
