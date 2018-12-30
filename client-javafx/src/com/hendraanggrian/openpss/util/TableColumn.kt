@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.FxComponent
import javafx.scene.control.Control
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.text.Text
import ktfx.finalBoolean
import ktfx.finalString
import ktfx.invoke
import ktfx.layouts.imageView
import ktfx.listeners.cellFactory

fun <T> TableColumn<T, Boolean>.doneCell(size: Int = 64, target: T.() -> Boolean) {
    size.toDouble().let {
        minWidth = it
        prefWidth = it
        maxWidth = it
    }
    isResizable = false
    style = "-fx-alignment: center;"
    setCellValueFactory { finalBoolean(it.value.target()) }
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
    setCellValueFactory { finalString(it.value.target().orEmpty()) }

fun <T> TableColumn<T, String>.numberCell(component: FxComponent, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { finalString(component.numberConverter(it.value.target())) }
}

fun <T> TableColumn<T, String>.currencyCell(component: FxComponent, target: T.() -> Double) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { finalString(component.currencyConverter(it.value.target())) }
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