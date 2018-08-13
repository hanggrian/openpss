package com.hendraanggrian.openpss.ui.main.edit.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos.CENTER_RIGHT
import javafxx.beans.property.toProperty
import javafxx.coroutines.onEditCommit
import javafxx.listeners.textFieldCellFactory
import javafxx.styles.labeledStyle
import kotlinx.nosql.equal
import kotlinx.nosql.update

@Suppress("UNCHECKED_CAST")
class EditOffsetPriceDialog(
    resourced: Resourced,
    employee: Employee
) : EditPriceDialog<OffsetPrice, OffsetPrices>(OffsetPrices, resourced, employee, R.string.offset_price) {

    init {
        getString(R.string.min_qty)<Int> {
            minWidth = 128.0
            style = labeledStyle { alignment = CENTER_RIGHT }
            setCellValueFactory { it.value.minQty.toProperty() as ObservableValue<Int> }
            textFieldCellFactory {
                fromString { it.toIntOrNull() ?: 0 }
            }
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }.projection { minQty }.update(cell.newValue)
                }
                cell.rowValue.minQty = cell.newValue
            }
        }

        getString(R.string.min_price)<Double> {
            minWidth = 128.0
            style = labeledStyle { alignment = CENTER_RIGHT }
            setCellValueFactory { it.value.minPrice.toProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }.projection { minPrice }.update(cell.newValue)
                }
                cell.rowValue.minPrice = cell.newValue
            }
        }

        getString(R.string.excess_price)<Double> {
            minWidth = 128.0
            style = labeledStyle { alignment = CENTER_RIGHT }
            setCellValueFactory { it.value.excessPrice.toProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    OffsetPrices { it.name.equal(cell.rowValue.name) }.projection { excessPrice }.update(cell.newValue)
                }
                cell.rowValue.excessPrice = cell.newValue
            }
        }
    }

    override fun newPrice(name: String): OffsetPrice = OffsetPrice.new(name)
}