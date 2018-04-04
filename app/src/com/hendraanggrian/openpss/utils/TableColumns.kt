@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
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

inline fun <T> TableColumn<T, String>.stringCell(noinline target: T.() -> Any) =
    setCellValueFactory { it.value.target().let { it as? String ?: it.toString() }.toProperty() }

inline fun <T> TableColumn<T, String>.numberCell(noinline target: T.() -> Int) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { numberConverter.toString(it.value.target()).toProperty() }
}

inline fun <T> TableColumn<T, String>.currencyCell(noinline target: T.() -> Double) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { currencyConverter.toString(it.value.target()).toProperty() }
}