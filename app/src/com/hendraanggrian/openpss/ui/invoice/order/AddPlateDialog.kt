package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.DoubleField
import com.hendraanggrian.openpss.controls.doubleField
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Plate
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Resourced
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

class AddPlateDialog(resourced: Resourced) : AddOrderDialog<Plate>(
    resourced,
    R.string.add_plate,
    R.image.ic_plate
) {
    private lateinit var typeChoice: ChoiceBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun _GridPane.onLayout() {
        label(getString(R.string.type)) col 0 row 2
        typeChoice = choiceBox(transaction { PlatePrices.find().toObservableList() }!!) {
            valueProperty().listener { _, _, plate ->
                priceField.value = plate.price
            }
        } col 1 row 2
        label(getString(R.string.price)) col 0 row 3
        priceField = doubleField { promptText = getString(R.string.price) } col 1 row 3
    }

    override val titleBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty, priceField.valueProperty)

    override val disableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty.lessEq(0) or
            priceField.valueProperty.lessEq(0)

    override fun newInstance(): Plate = Invoices.Plates.new(
        titleField.text,
        qtyField.value,
        typeChoice.value?.name ?: "",
        priceField.value)
}