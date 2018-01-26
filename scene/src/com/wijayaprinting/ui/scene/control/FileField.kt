@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import com.wijayaprinting.ui.scene.control.FileField.Scope
import com.wijayaprinting.ui.scene.control.FileField.Scope.FILE
import com.wijayaprinting.ui.scene.control.FileField.Scope.FOLDER
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import kotfx.*
import java.io.File

/** Field that display file or directory path. */
open class FileField @JvmOverloads constructor(scope: Scope = FILE) : TextField() {

    val fileProperty: ObjectProperty<File> = SimpleObjectProperty<File>()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        fileProperty bind bindingOf(textProperty()) { File(text) }
        validProperty bind booleanBindingOf(textProperty()) {
            !file.exists() || when (scope) {
                FILE -> !file.isFile
                FOLDER -> !file.isDirectory
                else -> false
            }
        }
    }

    var file: File
        get() = fileProperty.get()
        set(value) = fileProperty.set(value)

    var isValid: Boolean
        get() = validProperty.get()
        set(value) = validProperty.set(value)

    enum class Scope {
        FILE, FOLDER, ANY
    }
}

@JvmOverloads inline fun fileField(scope: Scope = FILE, noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.fileField(scope: Scope = FILE, noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.fileField(scope: Scope = FILE, noinline init: ((@LayoutDsl FileField).() -> Unit)? = null): FileField = FileField(scope).apply { init?.invoke(this) }.add()
