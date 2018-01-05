package com.wijayaprinting.manager.controller

import com.wijayaprinting.dao.Plate
import com.wijayaprinting.manager.App
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Refreshable
import com.wijayaprinting.manager.utils.safeTransaction
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
            event.consume()
            safeTransaction { event.rowValue.price = event.newValue }
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
        tableView.items.add(safeTransaction { Plate.new(id) {} })
    }

    @FXML
    fun deleteOnAction() = warningAlert(getString(R.string.delete_plate_warning), YES, NO)
            .showAndWait()
            .filter { it == YES }
            .ifPresent { tableView.items.remove(safeTransaction { tableView.selectionModel.selectedItem.apply { delete() } }) }

    override fun refresh() {
        tableView.items = safeTransaction { Plate.all() }!!.toMutableObservableList()
    }
}