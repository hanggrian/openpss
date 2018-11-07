package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.value.ObservableValue
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.toProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditDigitalPrintPriceDialog(
    context: Context
) : EditPriceDialog<DigitalPrice, DigitalPrices>(context, R.string.digital_print_price, DigitalPrices) {

    init {
        getString(R.string.one_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.oneSidePrice.toProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    DigitalPrices { it.name.equal(cell.rowValue.name) }
                        .projection { oneSidePrice }
                        .update(cell.newValue)
                }
                cell.rowValue.oneSidePrice = cell.newValue
            }
        }
        getString(R.string.two_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.twoSidePrice.toProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    DigitalPrices { it.name.equal(cell.rowValue.name) }
                        .projection { twoSidePrice }
                        .update(cell.newValue)
                }
                cell.rowValue.twoSidePrice = cell.newValue
            }
        }
    }

    override fun newPrice(name: String): DigitalPrice = DigitalPrice.new(name)
}