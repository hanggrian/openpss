package com.wijayaprinting.manager.controller

import com.wijayaprinting.dao.Reccess
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Refreshable
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import kotfx.asProperty
import kotfx.bind
import kotfx.dialog
import kotfx.toMutableObservableList
import org.joda.time.LocalTime

class AttendanceReccessController : Controller(), Refreshable {

    @FXML lateinit var deleteButton: Button

    @FXML lateinit var tableView: TableView<Reccess>
    @FXML lateinit var startColumn: TableColumn<Reccess, LocalTime>
    @FXML lateinit var endColumn: TableColumn<Reccess, LocalTime>

    @FXML
    fun initialize() {
        deleteButton.disableProperty() bind tableView.selectionModel.selectedItemProperty().isNull
        startColumn.setCellValueFactory { it.value.start.asProperty() }
        endColumn.setCellValueFactory { it.value.end.asProperty() }
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun addOnAction() = dialog<Pair<LocalTime, LocalTime>>(getString(R.string.add_reccess), getString(R.string.add_reccess), ImageView(R.png.ic_clock)) {

    }.showAndWait().ifPresent { (_start, _end) ->
        tableView.items.add(safeTransaction {
            Reccess.new {
                start = _start
                end = _end
            }
        })
    }

    @FXML
    fun deleteOnAction() = tableView.items.remove(safeTransaction { tableView.selectionModel.selectedItem.apply { delete() } })

    override fun refresh() {
        tableView.items = safeTransaction { Reccess.all().toMutableObservableList() }
    }
}