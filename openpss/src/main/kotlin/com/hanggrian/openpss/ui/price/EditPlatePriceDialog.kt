package com.hanggrian.openpss.ui.price

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.PlatePrice
import com.hanggrian.openpss.db.schemas.PlatePrices
import com.hanggrian.openpss.db.transaction
import javafx.beans.value.ObservableValue
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.cells.textFieldCellFactory
import ktfx.controls.TableColumnScope
import ktfx.coroutines.onEditCommit
import ktfx.doublePropertyOf
import ktfx.text.buildStringConverter

class EditPlatePriceDialog(context: Context) :
    EditPriceDialog<PlatePrice, PlatePrices>(context, R.string_plate_price, PlatePrices) {
    override fun onColumns(columns: TableColumnScope<PlatePrice>) {
        super.onColumns(columns)

        columns.append<Double>(getString(R.string_price)) {
            style = "-fx-alignment: center-right;"
            setCellValueFactory { doublePropertyOf(it.value.price) as ObservableValue<Double> }
            textFieldCellFactory(buildStringConverter { fromString { it.toDoubleOrNull() ?: 0.0 } })
            onEditCommit { cell ->
                transaction {
                    PlatePrices { it.name.equal(cell.rowValue.name) }
                        .projection { price }
                        .update(cell.newValue)
                }
                cell.rowValue.price = cell.newValue
            }
        }
    }

    override fun newPrice(name: String): PlatePrice = PlatePrice.new(name)
}
