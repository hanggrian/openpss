package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import javafx.scene.control.Control
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import ktfx.cells.cellFactory
import ktfx.finalBooleanProperty
import ktfx.finalStringProperty
import ktfx.util.invoke

fun <T> TableColumn<T, Boolean>.doneCell(size: Int = 64, target: T.() -> Boolean) {
    size.toDouble().let {
        minWidth = it
        prefWidth = it
        maxWidth = it
    }
    isResizable = false
    style = "-fx-alignment: center;"
    setCellValueFactory { finalBooleanProperty(it.value.target()) }
    cellFactory {
        onUpdate { done, empty ->
            text = null
            graphic = null
            if (done != null && !empty) graphic = ImageView(
                when {
                    done -> R.image.graphic_done_yes
                    else -> R.image.graphic_done_no
                }
            )
        }
    }
}

fun <T> TableColumn<T, String>.stringCell(target: T.() -> String?) =
    setCellValueFactory { finalStringProperty(it.value.target().orEmpty()) }

fun <T> TableColumn<T, String>.numberCell(component: FxComponent, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { finalStringProperty(component.numberConverter(it.value.target())) }
}

fun <T> TableColumn<T, String>.currencyCell(component: FxComponent, target: T.() -> Double) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { finalStringProperty(component.currencyConverter(it.value.target())) }
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
