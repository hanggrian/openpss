package com.wijayaprinting.ui.order

import com.wijayaprinting.db.dao.Plate
import com.wijayaprinting.db.schema.Plates
import com.wijayaprinting.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotfx.beans.property.asObservable
import kotfx.beans.property.toProperty
import kotfx.listeners.onEditCommit
import kotfx.listeners.textFieldCellFactory
import kotlinx.nosql.equal
import kotlinx.nosql.update

class PlatePriceController : PriceController<Plate, Plates>(Plates) {

    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    override fun initialize() {
        super.initialize()
        priceColumn.setCellValueFactory { it.value.price.toProperty().asObservable() }
        priceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        priceColumn.onEditCommit { event ->
            transaction { Plates.find { name.equal(event.rowValue.name) }.projection { price }.update(event.newValue) }
            event.rowValue.price = event.newValue
        }
    }

    override fun newPrice(name: String): Plate = Plate(name, 0.0)
}