@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.or
import kotfx.internal.ChildManager
import kotfx.internal.ControlDsl
import kotfx.internal.ItemManager
import kotfx.properties.bind
import java.io.File

open class FileField : TextField() {

    val validProperty = SimpleBooleanProperty()

    init {
        validProperty bind (textProperty().isEmpty or booleanBindingOf(textProperty()) {
            val file = File(text)
            !file.exists() || !file.isFile
        })
    }

    val isValid: Boolean get() = validProperty.value
}

@JvmOverloads
inline fun fileFieldOf(
        noinline init: ((@ControlDsl FileField).() -> Unit)? = null
): FileField = FileField().apply { init?.invoke(this) }

@JvmOverloads
inline fun ChildManager.fileField(
        noinline init: ((@ControlDsl FileField).() -> Unit)? = null
): FileField = FileField().apply { init?.invoke(this) }.add()

@JvmOverloads
inline fun ItemManager.fileField(
        noinline init: ((@ControlDsl FileField).() -> Unit)? = null
): FileField = FileField().apply { init?.invoke(this) }.add()