package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.db.SimpleOrder
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.localization.Resourced
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ChoiceBox
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.layouts._GridPane
import ktfx.layouts.choiceBox
import ktfx.layouts.label

class AddPlatePopover(resourced: Resourced) : AddOrderPopover<Invoice.Plate>(
    resourced,
    R.string.add_plate
), SimpleOrder {
    private lateinit var machineChoice: ChoiceBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun _GridPane.onLayout() {
        label(getString(R.string.machine)) col 0 row 2
        machineChoice = choiceBox(transaction { PlatePrices().toObservableList() }) {
            valueProperty().listener { _, _, plate ->
                priceField.value = plate.price
            }
        } col 1 row 2
        label(getString(R.string.price)) col 0 row 3
        priceField = doubleField { promptText = getString(R.string.price) } col 1 row 3
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val disableBinding: ObservableBooleanValue
        get() = machineChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            priceField.valueProperty().lessEq(0)

    override val optionalResult: Invoice.Plate?
        get() = Invoice.Plate.new(
            machineChoice.value.name,
            titleField.text,
            qtyField.value,
            priceField.value)

    override val qty: Int get() = qtyField.value

    override val price: Double get() = priceField.value
}