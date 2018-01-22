package com.wijayaprinting.controllers

import com.wijayaprinting.R
import com.wijayaprinting.db.Plate
import com.wijayaprinting.db.Plates
import com.wijayaprinting.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.asObservable
import kotfx.asProperty
import kotfx.stringConverter
import kotlinx.nosql.equal
import kotlinx.nosql.update

class PricePlateController : PriceController<Plate, Plates>(Plates, R.string.add_plate) {

    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    @FXML
    override fun initialize() {
        super.initialize()
        priceColumn.setCellValueFactory { it.value.price.asProperty().asObservable() }
        priceColumn.cellFactory = forTableColumn<Plate, Double>(stringConverter({ it.toDoubleOrNull() ?: 0.0 }))
        priceColumn.setOnEditCommit { event ->
            transaction { Plates.find { name.equal(event.rowValue.name) }.projection { price }.update(event.newValue) }
            event.rowValue.price = event.newValue
        }
    }

    override fun newPrice(name: String): Plate = Plate(name, 0.0)
}