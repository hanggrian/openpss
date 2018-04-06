@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import javafx.scene.control.TreeTableColumn
import ktfx.beans.property.toReadOnlyProperty

inline fun <T> TreeTableColumn<T, String>.stringCell(noinline target: T.() -> Any) =
    setCellValueFactory { it.value.value.target().let { it as? String ?: it.toString() }.toReadOnlyProperty() }