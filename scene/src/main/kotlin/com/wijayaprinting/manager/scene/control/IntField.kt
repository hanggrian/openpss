@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.digitsOnly
import com.wijayaprinting.manager.scene.utils.isDigits
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.internal.ChildManager
import kotfx.internal.ControlDsl
import kotfx.internal.ItemManager
import kotfx.stringConverter

open class IntField : TextField() {

    val valueProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ if (!isDigits) 0 else it.toInt() }))
        digitsOnly()
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

@JvmOverloads
inline fun intFieldOf(
        noinline init: ((@ControlDsl IntField).() -> Unit)? = null
): IntField = IntField().apply { init?.invoke(this) }

@JvmOverloads
inline fun ChildManager.intField(
        noinline init: ((@ControlDsl IntField).() -> Unit)? = null
): IntField = IntField().apply { init?.invoke(this) }.add()

@JvmOverloads
inline fun ItemManager.intField(
        noinline init: ((@ControlDsl IntField).() -> Unit)? = null
): IntField = IntField().apply { init?.invoke(this) }.add()