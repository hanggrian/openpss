@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Priced
import com.hendraanggrian.openpss.db.SplitPriced
import com.hendraanggrian.openpss.db.Totaled
import com.hendraanggrian.openpss.db.Typed
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.TableColumn
import javafx.scene.image.Image
import javafx.util.StringConverter
import ktfx.beans.property.toProperty
import ktfx.layouts.imageView
import ktfx.listeners.cellFactory
import ktfx.styles.labeledStyle

inline fun <T> TableColumn<T, Boolean>.doneCell(noinline target: T.() -> Boolean) {
    prefWidth = 48.0
    isResizable = false
    style = labeledStyle { alignment = CENTER }
    setCellValueFactory { it.value.target().toProperty() }
    cellFactory {
        onUpdate { done, empty ->
            clear()
            if (done != null && !empty) graphic = imageView(Image(when {
                done -> R.image.btn_done_yes
                else -> R.image.btn_done_no
            }))
        }
    }
}

inline fun <T : Typed> TableColumn<T, String>.typeCell() =
    setCellValueFactory { it.value.type.toProperty() }

inline fun <T : Order> TableColumn<T, String>.titleCell() =
    setCellValueFactory { it.value.title.toProperty() }

inline fun <T : Order> TableColumn<T, String>.qtyCell(converter: StringConverter<Number>) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { converter.toString(it.value.qty).toProperty() }
}

inline fun <T : Totaled> TableColumn<T, String>.totalCell(converter: StringConverter<Number>) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { converter.toString(it.value.total).toProperty() }
}

inline fun <T : Priced> TableColumn<T, String>.priceCell(converter: StringConverter<Number>) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { converter.toString(it.value.price).toProperty() }
}

inline fun <T : SplitPriced> TableColumn<T, String>.minQtyCell(converter: StringConverter<Number>) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { converter.toString(it.value.minQty).toProperty() }
}

inline fun <T : SplitPriced> TableColumn<T, String>.minPriceCell(converter: StringConverter<Number>) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { converter.toString(it.value.minPrice).toProperty() }
}

inline fun <T : SplitPriced> TableColumn<T, String>.excessPriceCell(converter: StringConverter<Number>) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { converter.toString(it.value.excessPrice).toProperty() }
}