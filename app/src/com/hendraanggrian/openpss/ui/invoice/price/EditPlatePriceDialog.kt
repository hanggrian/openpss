package com.hendraanggrian.openpss.ui.invoice.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.localization.Resourced
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.asObservable
import ktfx.beans.property.toProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

class EditPlatePriceDialog(
    resourced: Resourced,
    employee: Employee
) : EditPriceDialog<PlatePrice, PlatePrices>(PlatePrices, resourced, employee, R.string.plate_price) {

    init {
        getString(R.string.price)<Double> {
            setCellValueFactory { it.value.price.toProperty().asObservable() }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                transaction {
                    PlatePrices { it.name.equal(cell.rowValue.name) }.projection { price }.update(cell.newValue)
                }
                cell.rowValue.price = cell.newValue
            }
        }
    }

    override fun newPrice(name: String): PlatePrice = PlatePrice.new(name)
}