@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Priced
import com.hendraanggrian.openpss.db.SplitPriced
import com.hendraanggrian.openpss.db.Totaled
import com.hendraanggrian.openpss.db.Typed
import com.hendraanggrian.openpss.numberConverter
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.TableColumn
import javafx.scene.image.Image
import ktfx.beans.property.toProperty
import ktfx.layouts.imageView
import ktfx.listeners.cellFactory
import ktfx.styles.labeledStyle

fun <T> TableColumn<T, Boolean>.doneCell(size: Int = 64, target: T.() -> Boolean) {
    size.toDouble().let {
        minWidth = it
        prefWidth = it
        maxWidth = it
    }
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

inline fun <T : Order> TableColumn<T, String>.qtyCell() {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { numberConverter.toString(it.value.qty).toProperty() }
}

inline fun <T : Totaled> TableColumn<T, String>.totalCell() {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { currencyConverter.toString(it.value.total).toProperty() }
}

inline fun <T : Priced> TableColumn<T, String>.priceCell() {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { currencyConverter.toString(it.value.price).toProperty() }
}

inline fun <T : SplitPriced> TableColumn<T, String>.minQtyCell() {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { numberConverter.toString(it.value.minQty).toProperty() }
}

inline fun <T : SplitPriced> TableColumn<T, String>.minPriceCell() {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { currencyConverter.toString(it.value.minPrice).toProperty() }
}

inline fun <T : SplitPriced> TableColumn<T, String>.excessPriceCell() {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { currencyConverter.toString(it.value.excessPrice).toProperty() }
}