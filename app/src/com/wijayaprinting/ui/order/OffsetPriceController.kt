package com.wijayaprinting.ui.order

import com.wijayaprinting.db.dao.Offset
import com.wijayaprinting.db.dao.Offset.Companion.DEFAULT_AMOUNT
import com.wijayaprinting.db.schema.Offsets
import com.wijayaprinting.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.asObservable
import kotfx.stringConverterOf
import kotfx.toProperty
import kotlinx.nosql.equal
import kotlinx.nosql.update

class OffsetPriceController : PriceController<Offset, Offsets>(Offsets) {

    @FXML lateinit var minAmountColumn: TableColumn<Offset, Int>
    @FXML lateinit var minPriceColumn: TableColumn<Offset, Double>
    @FXML lateinit var excessPriceColumn: TableColumn<Offset, Double>

    override fun initialize() {
        super.initialize()
        minAmountColumn.setCellValueFactory { it.value.minAmount.toProperty().asObservable() }
        minAmountColumn.cellFactory = forTableColumn<Offset, Int>(stringConverterOf({ it.toIntOrNull() ?: 0 }))
        minAmountColumn.setOnEditCommit { event ->
            transaction { Offsets.find { name.equal(event.rowValue.name) }.projection { minAmount }.update(event.newValue) }
            event.rowValue.minAmount = event.newValue
        }

        minPriceColumn.setCellValueFactory { it.value.minPrice.toProperty().asObservable() }
        minPriceColumn.cellFactory = forTableColumn<Offset, Double>(stringConverterOf({ it.toDoubleOrNull() ?: 0.0 }))
        minPriceColumn.setOnEditCommit { event ->
            transaction { Offsets.find { name.equal(event.rowValue.name) }.projection { minPrice }.update(event.newValue) }
            event.rowValue.minPrice = event.newValue
        }

        excessPriceColumn.setCellValueFactory { it.value.excessPrice.toProperty().asObservable() }
        excessPriceColumn.cellFactory = forTableColumn<Offset, Double>(stringConverterOf({
            it.toDoubleOrNull() ?: 0.0
        }))
        excessPriceColumn.setOnEditCommit { event ->
            transaction { Offsets.find { name.equal(event.rowValue.name) }.projection { excessPrice }.update(event.newValue) }
            event.rowValue.excessPrice = event.newValue
        }
    }

    override fun newPrice(name: String): Offset = Offset(name, DEFAULT_AMOUNT, 0.0, 0.0)
}