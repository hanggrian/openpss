package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.FxComponent
import javafx.scene.control.TreeTableColumn
import ktfx.finalStringProperty
import ktfx.util.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) = setCellValueFactory { col ->
    finalStringProperty(col.value.value.target().let { it as? String ?: it.toString() })
}

fun <T> TreeTableColumn<T, String>.numberCell(component: FxComponent, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { finalStringProperty(component.numberConverter(it.value.value.target())) }
}
