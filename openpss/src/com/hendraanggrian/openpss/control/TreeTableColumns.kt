@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.numberConverter
import javafx.scene.control.TreeTableColumn
import ktfx.beans.property.toProperty
import ktfx.util.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) =
    setCellValueFactory { col -> col.value.value.target().let { it as? String ?: it.toString() }.toProperty() }

fun <T> TreeTableColumn<T, String>.numberCell(target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { numberConverter(it.value.value.target()).toProperty() }
}