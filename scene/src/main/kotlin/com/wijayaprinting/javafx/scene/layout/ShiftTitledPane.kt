package com.wijayaprinting.javafx.scene.layout

import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.getString
import com.wijayaprinting.javafx.safeTransaction
import com.wijayaprinting.javafx.scene.control.DoubleField
import com.wijayaprinting.javafx.scene.control.TimeField
import com.wijayaprinting.javafx.scene.utils.setGap
import com.wijayaprinting.mysql.dao.Shift
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.collections.toObservableList
import kotfx.dialogs.confirmAlert
import kotfx.dialogs.warningAlert
import kotfx.runLater
import org.joda.time.LocalTime
import java.math.BigDecimal

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class ShiftTitledPane : TitledPane() {

    val listView = ListView<Shift>()
    val addMenuItem = MenuItem(getString(R.string.add))
    val deleteMenuItem = MenuItem(getString(R.string.delete))

    init {
        content = listView
        contextMenu = ContextMenu(addMenuItem, deleteMenuItem)

        safeTransaction { listView.items = Shift.all().toList().toObservableList() }
        addMenuItem.setOnAction {
            AddDialog()
                    .showAndWait()
                    .ifPresent { (mStart, mEnd, mRecess) ->
                        safeTransaction {
                            Shift.new {
                                startTime = mStart
                                endTime = mEnd
                                recess = mRecess
                            }
                        }
                        // refreshShift()
                    }
        }
        deleteMenuItem.setOnAction {
            val shift = listView.selectionModel.selectedItem
            if (shift == null) warningAlert(getString(R.string.error_no_selection)).show()
            else confirmAlert(listView.selectionModel.selectedItem.toString(), getString(R.string.delete))
                    .showAndWait()
                    .filter { it == ButtonType.OK }
                    .ifPresent {
                        safeTransaction { shift.delete() }
                        // refreshShift()
                    }
        }
    }

    class AddDialog : Dialog<Triple<LocalTime, LocalTime, BigDecimal>>() {
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

        inner class Content : GridPane() {
            val startField = TimeField().apply {
                prefWidth = 64.0
                alignment = Pos.CENTER
            }
            val endField = TimeField().apply {
                prefWidth = 64.0
                alignment = Pos.CENTER
            }
            val recessField = DoubleField().apply {
                promptText = getString(R.string.hour)
                prefWidth = 64.0
                alignment = Pos.CENTER
            }

            init {
                setGap(8.0)
                add(Label(getString(R.string.shift)), 0, 0)
                add(startField, 1, 0)
                add(Label("-"), 2, 0)
                add(endField, 3, 0)
                add(Label(getString(R.string.recess)), 0, 1)
                add(recessField, 1, 1)
            }
        }
    }
}