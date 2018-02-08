@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import com.wijayaprinting.ui.scene.control.FileField.Scope
import com.wijayaprinting.ui.scene.control.FileField.Scope.FILE
import com.wijayaprinting.ui.scene.control.FileField.Scope.FOLDER
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.bindings.bindingOf
import kotfx.bindings.booleanBindingOf
import kotfx.properties.MutableAnyProperty
import kotfx.properties.MutableBooleanProperty
import kotfx.properties.SimpleAnyProperty
import kotfx.scene.ChildRoot
import kotfx.scene.ItemRoot
import java.io.File

/** Field that display file or directory path. */
open class FileField @JvmOverloads constructor(scope: Scope = FILE) : TextField() {

    val fileProperty: MutableAnyProperty<File> = SimpleAnyProperty<File>()
    val validProperty: MutableBooleanProperty = SimpleBooleanProperty()

    init {
        fileProperty.bind(bindingOf(textProperty()) { File(text) })
        validProperty.bind(booleanBindingOf(textProperty()) {
            !file.exists() || when (scope) {
                FILE -> !file.isFile
                FOLDER -> !file.isDirectory
                else -> false
            }
        })
    }

    val file: File get() = fileProperty.get()

    val isValid: Boolean get() = validProperty.get()

    enum class Scope {
        FILE, FOLDER, ANY
    }
}

@JvmOverloads inline fun fileField(scope: Scope = FILE, noinline init: ((@SceneDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.fileField(scope: Scope = FILE, noinline init: ((@SceneDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.fileField(scope: Scope = FILE, noinline init: ((@SceneDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }.add()
