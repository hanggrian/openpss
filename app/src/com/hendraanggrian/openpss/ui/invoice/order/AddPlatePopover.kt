package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ChoiceBox
import javafxx.beans.value.isBlank
import javafxx.beans.value.lessEq
import javafxx.beans.value.or
import javafxx.collections.toObservableList
import javafxx.coroutines.listener
import javafxx.layouts._GridPane
import javafxx.layouts.choiceBox
import javafxx.layouts.label

class AddPlatePopover(resourced: Resourced) : AddOrderPopover<Invoice.Plate>(resourced, R.string.add_plate),
    Invoice.Order {

    private lateinit var machineChoice: ChoiceBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.machine)) col 0 row currentRow
        machineChoice = choiceBox(transaction { PlatePrices().toObservableList() }) {
            valueProperty().listener { _, _, plate ->
                priceField.value = plate.price
            }
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.price)) col 0 row currentRow
        priceField = doubleField { promptText = getString(R.string.price) } col 1 colSpans 2 row currentRow
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = machineChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.Plate?
        get() = Invoice.Plate.new(machineChoice.value.name, titleField.text, qty, total)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}