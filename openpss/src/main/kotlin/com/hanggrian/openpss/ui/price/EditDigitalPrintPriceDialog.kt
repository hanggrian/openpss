package com.hanggrian.openpss.ui.price

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.DigitalPrice
import com.hanggrian.openpss.db.schemas.DigitalPrices
import com.hanggrian.openpss.db.transaction
import javafx.beans.value.ObservableValue
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.cells.textFieldCellFactory
import ktfx.coroutines.onEditCommit
import ktfx.doublePropertyOf
import ktfx.text.buildStringConverter

class EditDigitalPrintPriceDialog(context: Context) :
    EditPriceDialog<DigitalPrice, DigitalPrices>(
        context,
        R.string_digital_print_price,
        DigitalPrices,
    ) {
    init {
        getString(R.string_one_side_price)<Double> {
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                doublePropertyOf(it.value.oneSidePrice) as ObservableValue<Double>
            }
            textFieldCellFactory(buildStringConverter { fromString { it.toDoubleOrNull() ?: 0.0 } })
            onEditCommit { cell ->
                transaction {
                    DigitalPrices { it.name.equal(cell.rowValue.name) }
                        .projection { oneSidePrice }
                        .update(cell.newValue)
                }
                cell.rowValue.oneSidePrice = cell.newValue
            }
        }
        getString(R.string_two_side_price)<Double> {
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                doublePropertyOf(it.value.twoSidePrice) as ObservableValue<Double>
            }
            textFieldCellFactory(buildStringConverter { fromString { it.toDoubleOrNull() ?: 0.0 } })
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
