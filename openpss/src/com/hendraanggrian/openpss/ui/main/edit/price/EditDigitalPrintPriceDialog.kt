package com.hendraanggrian.openpss.ui.main.edit.price

import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.DigitalPrintPrice
import com.hendraanggrian.openpss.db.schemas.DigitalPrintPrices
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
) : EditPriceDialog<DigitalPrintPrice, DigitalPrintPrices>(context, R.string.digital_print_price, DigitalPrintPrices) {

    init {
        getString(R.string.one_sided_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.oneSidePrice.toProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    DigitalPrintPrices { it.name.equal(cell.rowValue.name) }
                        .projection { oneSidePrice }
                        .update(cell.newValue)
                }
                cell.rowValue.oneSidePrice = cell.newValue
            }
        }
        getString(R.string.two_sided_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.twoSidePrice.toProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    DigitalPrintPrices { it.name.equal(cell.rowValue.name) }
                        .projection { twoSidePrice }
                        .update(cell.newValue)
                }
                cell.rowValue.twoSidePrice = cell.newValue
            }
        }
    }

    override fun newPrice(name: String): DigitalPrintPrice = DigitalPrintPrice.new(name)
}