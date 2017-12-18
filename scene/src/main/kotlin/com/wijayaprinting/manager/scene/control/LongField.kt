@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.digitsOnly
import com.wijayaprinting.manager.scene.utils.isDigits
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.controls.ItemManager
import kotfx.internal.KotfxDsl
import kotfx.layouts.ChildManager
import kotfx.stringConverter

open class LongField : TextField() {

    val valueProperty = SimpleLongProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ if (!isDigits) 0 else it.toLong() }))
        digitsOnly()
    }

    var value: Long
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

@JvmOverloads
inline fun longField(
        noinline init: ((@KotfxDsl LongField).() -> Unit)? = null
): LongField = LongField().apply { init?.invoke(this) }

@JvmOverloads
inline fun ChildManager.longField(
        noinline init: ((@KotfxDsl LongField).() -> Unit)? = null
): LongField = LongField().apply { init?.invoke(this) }.add()

@JvmOverloads
inline fun ItemManager.longField(
        noinline init: ((@KotfxDsl LongField).() -> Unit)? = null
): LongField = LongField().apply { init?.invoke(this) }.add()