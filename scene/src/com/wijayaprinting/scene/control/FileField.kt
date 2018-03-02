@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.control

import com.wijayaprinting.scene.control.FileField.Scope
import com.wijayaprinting.scene.control.FileField.Scope.FILE
import com.wijayaprinting.scene.control.FileField.Scope.FOLDER
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import kotfx.annotations.LayoutDsl
import kotfx.beans.binding.bindingOf
import kotfx.beans.binding.booleanBindingOf
import kotfx.layout.ChildManager
import kotfx.layout.ItemManager
import java.io.File

/** Field that display file or directory path. */
open class FileField(scope: Scope = FILE) : TextField() {

    val fileProperty: ObjectProperty<File> = SimpleObjectProperty<File>()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

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

inline fun fileField(scope: Scope = FILE, noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }
inline fun ChildManager.fileField(scope: Scope = FILE, noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }.add()
inline fun ItemManager.fileField(scope: Scope = FILE, noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }.add()
