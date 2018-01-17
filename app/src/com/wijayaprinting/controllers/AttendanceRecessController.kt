package com.wijayaprinting.controllers

import com.wijayaprinting.PATTERN_TIME
import com.wijayaprinting.R
import com.wijayaprinting.core.Refreshable
import com.wijayaprinting.layouts.TimeBox
import com.wijayaprinting.layouts.timeBox
import com.wijayaprinting.nosql.Recess
import com.wijayaprinting.nosql.Recesses
import com.wijayaprinting.nosql.transaction
import com.wijayaprinting.util.gap
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.*
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.id
import org.joda.time.LocalTime

class AttendanceRecessController : Controller(), Refreshable {

    @FXML lateinit var deleteButton: Button

    @FXML lateinit var recessTable: TableView<Recess>
    @FXML lateinit var startColumn: TableColumn<Recess, String>
    @FXML lateinit var endColumn: TableColumn<Recess, String>

    @FXML
    fun initialize() {
        deleteButton.disableProperty() bind recessTable.selectionModel.selectedItemProperty().isNull
        startColumn.setCellValueFactory { it.value.start.toString(PATTERN_TIME).asProperty() }
        endColumn.setCellValueFactory { it.value.end.toString(PATTERN_TIME).asProperty() }
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun addOnAction() = dialog<Pair<LocalTime, LocalTime>>(getString(R.string.add_reccess), getString(R.string.add_reccess), ImageView(R.png.ic_clock)) {
        lateinit var startBox: TimeBox
        lateinit var endBox: TimeBox
        content = gridPane {
            gap(8)
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        button(CANCEL)
        button(OK).disableProperty() bind booleanBindingOf(startBox.timeProperty, endBox.timeProperty) { startBox.time >= endBox.time }
        setResultConverter { if (it == OK) Pair(startBox.time, endBox.time) else null }
    }.showAndWait().ifPresent { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(Recess(start, end)) }!!
        recessTable.items.add(recess)
    }

    @FXML
    fun deleteOnAction() = confirmAlert(getString(R.string.are_you_sure), YES, NO)
            .showAndWait()
            .filter { it == YES }
            .ifPresent {
                recessTable.selectionModel.selectedItem.let { recess ->
                    transaction { Recesses.find { id.equal(recess.id.value) }.remove() }
                    recessTable.items.remove(recess)
                }
            }

    override fun refresh() {
        recessTable.items = transaction { Recesses.find().toMutableObservableList() }
    }
}