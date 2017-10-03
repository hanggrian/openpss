package com.wijayaprinting.javafx.scene.layout

import com.wijayaprinting.javafx.BuildConfig
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.getString
import com.wijayaprinting.javafx.safeTransaction
import com.wijayaprinting.javafx.scene.Updatable
import com.wijayaprinting.javafx.scene.control.DoubleField
import com.wijayaprinting.javafx.scene.control.TimeField
import com.wijayaprinting.javafx.scene.utils.gaps
import com.wijayaprinting.mysql.dao.Shift
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.util.Callback
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.collections.toMutableObservableList
import kotfx.dialogs.confirmAlert
import kotfx.dialogs.dialog
import kotfx.runLater
import org.joda.time.LocalTime
import java.math.BigDecimal

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class ShiftTitledPane : TitledPane(), Updatable {

    override fun update() {
        listView.items = Shift.all().toList().toMutableObservableList()
    }

    val listView = ListView<Shift>()
    val addMenuItem = MenuItem(getString(R.string.add))
    val deleteMenuItem = MenuItem(getString(R.string.delete)).apply {
        visibleProperty().bind(listView.selectionModel.selectedItemProperty().isNotNull)
    }

    init {
        isCollapsible = false
        content = listView
        contextMenu = ContextMenu(addMenuItem, deleteMenuItem)
        addMenuItem.setOnAction {
            dialog<Triple<LocalTime, LocalTime, BigDecimal>>(getString(R.string.shift), ImageView(R.png.ic_clock), getString(R.string.shift)) {
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
                buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
                lookupButton(ButtonType.OK).disableProperty().bind(not(startField.validProperty)
                        or not(endField.validProperty)
                        or not(recessField.validProperty))
                content = GridPane().apply {
                    gaps = 8.0
                    add(Label(getString(R.string.time)), 0, 0)
                    add(startField, 1, 0)
                    add(Label("-"), 2, 0)
                    add(endField, 3, 0)
                    add(Label(getString(R.string.recess)), 0, 1)
                    add(recessField, 1, 1)
                }
                runLater { startField.requestFocus() }
                Callback {
                    when (it) {
                        ButtonType.OK -> Triple(startField.value!!, endField.value!!, BigDecimal(recessField.value))
                        else -> null
                    }
                }
            }.showAndWait()
                    .ifPresent { (mStart, mEnd, mRecess) ->
                        safeTransaction {
                            Shift.new {
                                startTime = mStart
                                endTime = mEnd
                                recess = mRecess
                            }
                            update()
                        }
                    }
        }
        deleteMenuItem.setOnAction {
            confirmAlert(listView.selectionModel.selectedItem.toString(), getString(R.string.delete))
                    .showAndWait()
                    .filter { it == ButtonType.OK }
                    .ifPresent {
                        safeTransaction {
                            listView.selectionModel.selectedItem.delete()
                            update()
                        }
                    }
        }
        if (!BuildConfig.DEBUG) {
            safeTransaction { update() }
        }
    }
}