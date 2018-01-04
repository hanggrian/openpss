package com.wijayaprinting.manager.controller

import com.hendraanggrian.rxexposed.SQLSingles
import com.wijayaprinting.dao.Plate
import com.wijayaprinting.manager.App
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Refreshable
import com.wijayaprinting.manager.utils.multithread
import io.reactivex.rxkotlin.subscribeBy
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.*
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class PlatePriceController : Controller(), Refreshable {

    @FXML lateinit var deleteButton: Button

    @FXML lateinit var tableView: TableView<Plate>
    @FXML lateinit var idColumn: TableColumn<Plate, String>
    @FXML lateinit var priceColumn: TableColumn<Plate, BigDecimal>

    @FXML
    fun initialize() {
        deleteButton.disableProperty() bind (tableView.selectionModel.selectedItemProperty().isNull or !App.fullAccess.asProperty())
        idColumn.setCellValueFactory { it.value.id.value.asProperty() }
        priceColumn.setCellValueFactory { it.value.price.asProperty() }
        priceColumn.cellFactory = forTableColumn<Plate, BigDecimal>(stringConverter({ it.toBigDecimalOrNull() ?: ZERO }))
        priceColumn.setOnEditCommit { event ->
            SQLSingles.transaction { event.rowValue.price = event.newValue }
                    .multithread()
                    .subscribeBy({
                        event.consume()
                        errorAlert(it.message.toString()).showAndWait()
                    }) {}
                    .register()
        }
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun addOnAction() = inputDialog {
        title = getString(R.string.add_plate)
        headerText = getString(R.string.add_plate)
        contentText = getString(R.string.name)
        editor.promptText = getString(R.string.plate)
    }.showAndWait().ifPresent { id ->
        SQLSingles.transaction { Plate.new(id) {} }
                .multithread()
                .subscribeBy({ errorAlert(it.message.toString()).showAndWait() }) { plate -> tableView.items.add(plate) }
                .register()
    }

    @FXML
    fun deleteOnAction() = warningAlert(getString(R.string.delete_plate_warning), YES, NO)
            .showAndWait()
            .filter { it == YES }
            .ifPresent {
                SQLSingles.transaction { tableView.selectionModel.selectedItem.apply { delete() } }
                        .multithread()
                        .subscribeBy({ errorAlert(it.message.toString()).showAndWait() }) { plate -> tableView.items.remove(plate) }
                        .register()
            }

    override fun refresh() = SQLSingles.transaction { Plate.all().toMutableObservableList() }
            .subscribeBy({}) { plates -> tableView.items = plates }
            .register()
}