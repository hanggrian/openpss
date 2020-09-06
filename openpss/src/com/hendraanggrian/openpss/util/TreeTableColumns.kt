@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.content.Context
import javafx.scene.control.TreeTableColumn
import ktfx.stringPropertyOf
import ktfx.text.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) =
    setCellValueFactory { col -> stringPropertyOf(col.value.value.target().let { it as? String ?: it.toString() }) }

fun <T> TreeTableColumn<T, String>.numberCell(context: Context, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { stringPropertyOf(context.numberConverter(it.value.value.target())) }
}
