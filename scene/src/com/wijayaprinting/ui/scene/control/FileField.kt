@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.*
import java.io.File

/** Field that display file or directory path. */
open class FileField : TextField() {

    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        validProperty bind (textProperty().isEmpty or booleanBindingOf(textProperty()) {
            val file = File(text)
            !file.exists() || !file.isFile
        })
    }

    var isValid: Boolean
        get() = validProperty.get()
        set(value) = validProperty.set(value)
}

@JvmOverloads inline fun fileField(noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.fileField(noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.fileField(noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField().apply { init?.invoke(this) }.add()
