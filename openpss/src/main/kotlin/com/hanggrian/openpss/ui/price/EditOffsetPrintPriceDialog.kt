package com.hanggrian.openpss.ui.price

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.OffsetPrice
import com.hanggrian.openpss.db.schemas.OffsetPrices
import com.hanggrian.openpss.db.transaction
import javafx.beans.value.ObservableValue
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.cells.textFieldCellFactory
import ktfx.controls.TableColumnScope
import ktfx.coroutines.onEditCommit
import ktfx.doublePropertyOf
import ktfx.intPropertyOf
import ktfx.text.buildStringConverter

class EditOffsetPrintPriceDialog(context: Context) :
    EditPriceDialog<OffsetPrice, OffsetPrices>(context, R.string_offset_print_price, OffsetPrices) {
    override fun onColumns(columns: TableColumnScope<OffsetPrice>) {
        super.onColumns(columns)

        columns.append<Int>(getString(R.string_min_qty)) {
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                intPropertyOf(it.value.minQty) as ObservableValue<Int>
            }
            textFieldCellFactory(buildStringConverter { fromString { it.toIntOrNull() ?: 0 } })
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }
                        .projection { minQty }
                        .update(cell.newValue)
                }
                cell.rowValue.minQty = cell.newValue
            }
        }
        columns.append<Double>(getString(R.string_min_price)) {
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                doublePropertyOf(it.value.minPrice) as ObservableValue<Double>
            }
            textFieldCellFactory(buildStringConverter { fromString { it.toDoubleOrNull() ?: 0.0 } })
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }
                        .projection { minPrice }
                        .update(cell.newValue)
                }
                cell.rowValue.minPrice = cell.newValue
            }
        }
        columns.append<Double>(getString(R.string_excess_price)) {
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                doublePropertyOf(it.value.excessPrice) as ObservableValue<Double>
            }
            textFieldCellFactory(buildStringConverter { fromString { it.toDoubleOrNull() ?: 0.0 } })
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
