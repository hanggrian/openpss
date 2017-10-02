package com.wijayaprinting.javafx.dialog

import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.control.field.DoubleField
import com.wijayaprinting.javafx.control.field.TimeField
import com.wijayaprinting.javafx.layout.GridPane
import com.wijayaprinting.javafx.utils.getString
import javafx.geometry.Pos
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.runLater
import org.joda.time.LocalTime
import java.math.BigDecimal

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class ShiftDialog : Dialog<Triple<LocalTime, LocalTime, BigDecimal>>() {

    val content = Content()

    init {
        title = getString(R.string.shift)
        graphic = ImageView(R.png.ic_shift)
        headerText = getString(R.string.shift)

        dialogPane.content = content
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.lookupButton(ButtonType.OK).disableProperty().bind(not(content.startField.validProperty)
                or not(content.endField.validProperty)
                or not(content.recessField.validProperty))
        runLater { content.startField.requestFocus() }
        setResultConverter {
            when (it) {
                ButtonType.OK -> Triple(content.startField.value!!, content.endField.value!!, BigDecimal(content.recessField.value))
                else -> null
            }
        }
    }

    inner class Content : GridPane(8.0) {
        val startField = TimeField().apply {
            prefWidth = 64.0
            alignment = Pos.CENTER
        }
        val endField = TimeField().apply {
            prefWidth = 64.0
            alignment = Pos.CENTER
        }
        val recessField = DoubleField(getString(R.string.hour)).apply {
            prefWidth = 64.0
            alignment = Pos.CENTER
        }

        init {
            add(Label(getString(R.string.shift)), 0, 0)
            add(startField, 1, 0)
            add(Label("-"), 2, 0)
            add(endField, 3, 0)
            add(Label(getString(R.string.recess)), 0, 1)
            add(recessField, 1, 1)
        }
    }
}