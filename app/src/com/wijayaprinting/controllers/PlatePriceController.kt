package com.wijayaprinting.controllers

import com.wijayaprinting.R
import com.wijayaprinting.core.Refreshable
import com.wijayaprinting.nosql.Plate
import com.wijayaprinting.nosql.Plates
import com.wijayaprinting.nosql.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.update

class PlatePriceController : Controller(), Refreshable {

    @FXML lateinit var tableView: TableView<Plate>
    @FXML lateinit var nameColumn: TableColumn<Plate, String>
    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    @FXML
    fun initialize() {
        nameColumn.setCellValueFactory { it.value.name.asProperty() }
        priceColumn.setCellValueFactory { it.value.price.asProperty().asObservable() }
        priceColumn.cellFactory = forTableColumn<Plate, Double>(stringConverter({ it.toDoubleOrNull() ?: 0.0 }))
        priceColumn.setOnEditCommit { event -> transaction { Plates.find { name.equal(event.rowValue.name) }.projection { price }.update(event.newValue) } }
        refresh()
    }

    @FXML fun refreshOnAction() = refresh()

    @FXML
    fun addOnAction() = inputDialog {
        title = getString(R.string.add_plate)
        headerText = getString(R.string.add_plate)
        contentText = getString(R.string.name)
        editor.promptText = getString(R.string.plate)
    }.showAndWait().ifPresent { name ->
        Plate(name, 0.0).let {
            transaction { Plates.insert(it) }
            tableView.items.add(it)
        }
    }

    override fun refresh() {
        tableView.items = transaction { Plates.find().toMutableObservableList() }
    }
}