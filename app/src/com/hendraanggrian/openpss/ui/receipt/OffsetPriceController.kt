package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.db.schema.OffsetPrice
import com.hendraanggrian.openpss.db.schema.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.asObservable
import ktfx.beans.property.toProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory
import java.net.URL
import java.util.ResourceBundle

class OffsetPriceController : PriceController<OffsetPrice, OffsetPrices>(OffsetPrices) {

    @FXML lateinit var minQtyColumn: TableColumn<OffsetPrice, Int>
    @FXML lateinit var minPriceColumn: TableColumn<OffsetPrice, Double>
    @FXML lateinit var excessPriceColumn: TableColumn<OffsetPrice, Double>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        minQtyColumn.setCellValueFactory { it.value.minQty.toProperty().asObservable() }
        minQtyColumn.textFieldCellFactory {
            fromString { it.toIntOrNull() ?: 0 }
        }
        minQtyColumn.onEditCommit {
            transaction {
                OffsetPrices.find { name.equal(it.rowValue.name) }.projection { minQty }.update(it.newValue)
            }
            it.rowValue.minQty = it.newValue
        }

        minPriceColumn.setCellValueFactory { it.value.minPrice.toProperty().asObservable() }
        minPriceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        minPriceColumn.onEditCommit {
            transaction {
                OffsetPrices.find { name.equal(it.rowValue.name) }.projection { minPrice }.update(it.newValue)
            }
            it.rowValue.minPrice = it.newValue
        }

        excessPriceColumn.setCellValueFactory { it.value.excessPrice.toProperty().asObservable() }
        excessPriceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        excessPriceColumn.onEditCommit {
            transaction {
                OffsetPrices.find { name.equal(it.rowValue.name) }.projection { excessPrice }.update(it.newValue)
            }
            it.rowValue.excessPrice = it.newValue
        }
    }

    override fun newPrice(name: String): OffsetPrice = OffsetPrice(name)
}