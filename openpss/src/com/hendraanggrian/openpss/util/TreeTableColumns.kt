@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.content.Context
import javafx.scene.control.TreeTableColumn
import ktfx.beans.property.asProperty
import ktfx.util.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) =
    setCellValueFactory { col -> col.value.value.target().let { it as? String ?: it.toString() }.asProperty() }

fun <T> TreeTableColumn<T, String>.numberCell(context: Context, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { context.numberConverter(it.value.value.target()).asProperty() }
}