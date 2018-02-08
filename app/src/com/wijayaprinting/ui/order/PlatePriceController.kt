package com.wijayaprinting.ui.order

import com.wijayaprinting.db.dao.Plate
import com.wijayaprinting.db.schema.Plates
import com.wijayaprinting.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.properties.asObservable
import kotfx.properties.toProperty
import kotfx.stringConverterOf
import kotlinx.nosql.equal
import kotlinx.nosql.update

class PlatePriceController : PriceController<Plate, Plates>(Plates) {

    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    override fun initialize() {
        super.initialize()
        priceColumn.setCellValueFactory { it.value.price.toProperty().asObservable() }
        priceColumn.cellFactory = forTableColumn<Plate, Double>(stringConverterOf { it.toDoubleOrNull() ?: 0.0 })
        priceColumn.setOnEditCommit { event ->
            transaction { Plates.find { name.equal(event.rowValue.name) }.projection { price }.update(event.newValue) }
            event.rowValue.price = event.newValue
        }
    }

    override fun newPrice(name: String): Plate = Plate(name, 0.0)
}