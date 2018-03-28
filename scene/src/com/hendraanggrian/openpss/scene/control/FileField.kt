@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import com.hendraanggrian.openpss.scene.control.FileField.Scope
import com.hendraanggrian.openpss.scene.control.FileField.Scope.FILE
import com.hendraanggrian.openpss.scene.control.FileField.Scope.FOLDER
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.value.getValue
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import java.io.File

/** Field that display file or directory path. */
open class FileField(scope: Scope = FILE) : TextField() {

    val fileProperty: ObjectProperty<File> = SimpleObjectProperty<File>()
    val file: File by fileProperty

    val validProperty: BooleanProperty = SimpleBooleanProperty()
    val isValid: Boolean by validProperty

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

    enum class Scope {
        FILE, FOLDER, ANY
    }
}

inline fun fileField(
    scope: Scope = FILE,
    noinline init: ((@LayoutDsl FileField).() -> Unit)? = null
): FileField = FileField(scope).apply { init?.invoke(this) }

inline fun LayoutManager<Node>.fileField(
    scope: Scope = FILE,
    noinline init: ((@LayoutDsl FileField).() -> Unit)? = null
): FileField = com.hendraanggrian.openpss.scene.control.fileField(scope, init).add()