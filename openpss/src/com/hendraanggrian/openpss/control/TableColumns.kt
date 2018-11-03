@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.currencyConverter
import com.hendraanggrian.openpss.content.numberConverter
import javafx.scene.control.Control
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.text.Text
import ktfx.beans.property.toProperty
import ktfx.layouts.imageView
import ktfx.listeners.cellFactory
import ktfx.util.invoke

fun <T> TableColumn<T, Boolean>.doneCell(size: Int = 64, target: T.() -> Boolean) {
    size.toDouble().let {
        minWidth = it
        prefWidth = it
        maxWidth = it
    }
    isResizable = false
    style = "-fx-alignment: center;"
    setCellValueFactory { it.value.target().toProperty() }
    cellFactory {
        onUpdate { done, empty ->
            text = null
            graphic = null
            if (done != null && !empty) graphic = imageView(
                when {
                    done -> R.image.btn_done_yes
                    else -> R.image.btn_done_no
                }
            )
        }
    }
}

fun <T> TableColumn<T, String>.stringCell(target: T.() -> String?) =
    setCellValueFactory { it.value.target().orEmpty().toProperty() }

fun <T> TableColumn<T, String>.numberCell(target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { numberConverter(it.value.target()).toProperty() }
}

fun <T> TableColumn<T, String>.currencyCell(target: T.() -> Double) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { currencyConverter(it.value.target()).toProperty() }
}

fun <S> TableColumn<S, String>.wrapText() = setCellFactory {
    val cell = TableCell<S, String>()
    val text = Text()
    cell.graphic = text
    cell.prefHeight = Control.USE_COMPUTED_SIZE
    text.wrappingWidthProperty().bind(widthProperty())
    text.textProperty().bind(cell.itemProperty())
    cell
}