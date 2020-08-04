package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.value.ObservableValue
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.asReadOnlyProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditOffsetPrintPriceDialog(
    context: Context
) : EditPriceDialog<OffsetPrice, OffsetPrices>(context, R.string.offset_print_price, OffsetPrices) {

    init {
        getString(R.string.min_qty)<Int> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.minQty.asReadOnlyProperty() as ObservableValue<Int> }
            textFieldCellFactory {
                fromString { it.toIntOrNull() ?: 0 }
            }
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }
                        .projection { minQty }
                        .update(cell.newValue)
                }
                cell.rowValue.minQty = cell.newValue
            }
        }

        getString(R.string.min_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.minPrice.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }
                        .projection { minPrice }
                        .update(cell.newValue)
                }
                cell.rowValue.minPrice = cell.newValue
            }
        }

        getString(R.string.excess_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.excessPrice.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }
                        .projection { excessPrice }
                        .update(cell.newValue)
                }
                cell.rowValue.excessPrice = cell.newValue
            }
        }
    }

    override fun newPrice(name: String): OffsetPrice = OffsetPrice.new(name)
}
