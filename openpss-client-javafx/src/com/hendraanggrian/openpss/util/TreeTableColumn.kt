package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.FxComponent
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.scene.control.TreeTableColumn
import ktfx.text.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) = setCellValueFactory { col ->
    ReadOnlyStringWrapper(col.value.value.target().let { it as? String ?: it.toString() })
}

fun <T> TreeTableColumn<T, String>.numberCell(component: FxComponent, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { ReadOnlyStringWrapper(component.numberConverter(it.value.value.target())) }
}
