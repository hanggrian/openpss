package com.hendraanggrian.openpss.ui.main.edit.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos.CENTER_RIGHT
import kotlinx.nosql.equal
import kotlinx.nosql.update
import javafxx.beans.property.toProperty
import javafxx.coroutines.onEditCommit
import javafxx.listeners.textFieldCellFactory
import javafxx.styles.labeledStyle

@Suppress("UNCHECKED_CAST")
class EditPlatePriceDialog(
    resourced: Resourced,
    employee: Employee
) : EditPriceDialog<PlatePrice, PlatePrices>(PlatePrices, resourced, employee, R.string.plate_price) {

    init {
        getString(R.string.price)<Double> {
            minWidth = 128.0
            style = labeledStyle { alignment = CENTER_RIGHT }
            setCellValueFactory { it.value.price.toProperty() as ObservableValue<Double> }
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