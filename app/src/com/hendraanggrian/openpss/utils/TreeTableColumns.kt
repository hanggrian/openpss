@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import javafx.geometry.Pos
import javafx.scene.control.TreeTableColumn
import ktfx.beans.property.toReadOnlyProperty
import ktfx.styles.labeledStyle

inline fun <T> TreeTableColumn<T, String>.stringCell(noinline target: T.() -> Any) =
    setCellValueFactory { it.value.value.target().let { it as? String ?: it.toString() }.toReadOnlyProperty() }

inline fun <T> TreeTableColumn<T, String>.numberCell(noinline target: T.() -> Int) {
    style = labeledStyle { alignment = Pos.CENTER_RIGHT }
    setCellValueFactory { numberConverter.toString(it.value.value.target()).toReadOnlyProperty() }
}