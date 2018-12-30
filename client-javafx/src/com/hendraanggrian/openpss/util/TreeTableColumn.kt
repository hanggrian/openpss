@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.ui.FxComponent
import javafx.scene.control.TreeTableColumn
import ktfx.finalString
import ktfx.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) =
    setCellValueFactory { col -> finalString(col.value.value.target().let { it as? String ?: it.toString() }) }

fun <T> TreeTableColumn<T, String>.numberCell(component: FxComponent, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { finalString(component.numberConverter(it.value.value.target())) }
}