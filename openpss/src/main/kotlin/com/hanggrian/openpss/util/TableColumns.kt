package com.hanggrian.openpss.util

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import javafx.scene.control.Control
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import ktfx.booleanPropertyOf
import ktfx.cells.cellFactory
import ktfx.stringPropertyOf
import ktfx.text.invoke

fun <T> TableColumn<T, Boolean>.doneCell(size: Int = 64, target: T.() -> Boolean) {
    size.toDouble().let {
        minWidth = it
        prefWidth = it
        maxWidth = it
    }
    isResizable = false
    style = "-fx-alignment: center;"
    setCellValueFactory { booleanPropertyOf(it.value.target()) }
    cellFactory {
        onUpdate { done, empty ->
            text = null
            graphic = null
            if (done != null && !empty) {
                graphic = ImageView(if (done) R.image_btn_done_yes else R.image_btn_done_no)
            }
        }
    }
}

fun <T> TableColumn<T, String>.stringCell(target: T.() -> String?) =
    setCellValueFactory {
        stringPropertyOf(it.value.target().orEmpty())
    }

fun <T> TableColumn<T, String>.numberCell(context: Context, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory {
        stringPropertyOf(context.numberConverter(it.value.target()))
    }
}

fun <T> TableColumn<T, String>.currencyCell(context: Context, target: T.() -> Double) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory {
        stringPropertyOf(context.currencyConverter(it.value.target()))
    }
}

fun <T> TableColumn<T, String>.wrapText() =
    setCellFactory {
        val cell = TableCell<T, String>()
        val text = Text()
        cell.graphic = text
        cell.prefHeight = Control.USE_COMPUTED_SIZE
        text.wrappingWidthProperty().bind(widthProperty())
        text.textProperty().bind(cell.itemProperty())
        cell
    }
