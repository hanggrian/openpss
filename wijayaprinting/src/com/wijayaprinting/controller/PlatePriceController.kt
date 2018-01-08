package com.wijayaprinting.controller

import com.wijayaprinting.R
import com.wijayaprinting.Refreshable
import com.wijayaprinting.dao.Plate
import com.wijayaprinting.utils.expose
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.asProperty
import kotfx.inputDialog
import kotfx.stringConverter
import kotfx.toMutableObservableList
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class PlatePriceController : Controller(), Refreshable {

    @FXML lateinit var tableView: TableView<Plate>
    @FXML lateinit var idColumn: TableColumn<Plate, String>
    @FXML lateinit var priceColumn: TableColumn<Plate, BigDecimal>

    @FXML
    fun initialize() {
        idColumn.setCellValueFactory { it.value.id.value.asProperty() }
        priceColumn.setCellValueFactory { it.value.price.asProperty() }
        priceColumn.cellFactory = forTableColumn<Plate, BigDecimal>(stringConverter({ it.toBigDecimalOrNull() ?: ZERO }))
        priceColumn.setOnEditCommit { event -> expose { event.rowValue.price = event.newValue } }
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
        tableView.items.add(expose { Plate.new(id) {} })
    }

    override fun refresh() {
        tableView.items = expose { Plate.all().toMutableObservableList() }
    }
}