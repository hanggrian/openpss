@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.*
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

@JvmOverloads inline fun fileField(noinline init: ((@KotfxDsl FileField).() -> Unit)? = null): FileField = FileField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.fileField(noinline init: ((@KotfxDsl FileField).() -> Unit)? = null): FileField = FileField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.fileField(noinline init: ((@KotfxDsl FileField).() -> Unit)? = null): FileField = FileField().apply { init?.invoke(this) }.add()