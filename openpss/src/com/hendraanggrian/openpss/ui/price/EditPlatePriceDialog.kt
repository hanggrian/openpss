package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.value.ObservableValue
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.asReadOnlyProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditPlatePriceDialog(
    context: Context
) : EditPriceDialog<PlatePrice, PlatePrices>(context, R.string.plate_price, PlatePrices) {

    init {
        getString(R.string.price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.price.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
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
