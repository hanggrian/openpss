package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.controls.FileField.Scope.FILE
import com.hendraanggrian.openpss.controls.FileField.Scope.FOLDER
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.value.getValue
import java.io.File

/** Field that display file or directory path. */
open class FileField @JvmOverloads constructor(scope: Scope = FILE) : TextField() {

    private val valueProperty = SimpleObjectProperty<File>()
    fun valueProperty(): ObjectProperty<File> = valueProperty
    val value: File by valueProperty

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty
    val isValid: Boolean by validProperty

    init {
        valueProperty.bind(bindingOf(textProperty()) { File(text) })
        validProperty.bind(booleanBindingOf(textProperty()) {
            !value.exists() || when (scope) {
                FILE -> !value.isFile
                FOLDER -> !value.isDirectory
                else -> false
            }
        })
    }

    enum class Scope {
        FILE, FOLDER, ANY
    }
}