@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.db.Priced
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.SplitPriced
import com.hendraanggrian.openpss.db.Typed
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.TableColumn
import javafx.util.StringConverter
import ktfx.beans.property.toProperty
import ktfx.styles.labeledStyle

inline fun <T : Typed> TableColumn<T, String>.typeCellValueFactory() =
    setCellValueFactory { it.value.type.toProperty() }

inline fun <T : Order> TableColumn<T, String>.titleCellValueFactory() =
    setCellValueFactory { it.value.title.toProperty() }

inline fun <T : Order> TableColumn<T, String>.qtyCellValueFactory(converter: StringConverter<Number>) {
    setCellValueFactory { converter.toString(it.value.qty).toProperty() }
    style = labeledStyle { alignment = CENTER_RIGHT }
}

inline fun <T : Order> TableColumn<T, String>.totalCellValueFactory(converter: StringConverter<Number>) {
    setCellValueFactory { converter.toString(it.value.total).toProperty() }
    style = labeledStyle { alignment = CENTER_RIGHT }
}

inline fun <T : Priced> TableColumn<T, String>.priceCellValueFactory(converter: StringConverter<Number>) {
    setCellValueFactory { converter.toString(it.value.price).toProperty() }
    style = labeledStyle { alignment = CENTER_RIGHT }
}

inline fun <T : SplitPriced> TableColumn<T, String>.minQtyCellValueFactory(converter: StringConverter<Number>) {
    setCellValueFactory { converter.toString(it.value.minQty).toProperty() }
    style = labeledStyle { alignment = CENTER_RIGHT }
}

inline fun <T : SplitPriced> TableColumn<T, String>.minPriceCellValueFactory(converter: StringConverter<Number>) {
    setCellValueFactory { converter.toString(it.value.minPrice).toProperty() }
    style = labeledStyle { alignment = CENTER_RIGHT }
}

inline fun <T : SplitPriced> TableColumn<T, String>.excessPriceCellValueFactory(converter: StringConverter<Number>) {
    setCellValueFactory { converter.toString(it.value.excessPrice).toProperty() }
    style = labeledStyle { alignment = CENTER_RIGHT }
}