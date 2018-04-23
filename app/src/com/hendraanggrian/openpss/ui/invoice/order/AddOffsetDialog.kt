package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.DoubleField
import com.hendraanggrian.openpss.controls.IntField
import com.hendraanggrian.openpss.controls.doubleField
import com.hendraanggrian.openpss.controls.intField
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Offset
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
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

class AddOffsetDialog(resourced: Resourced) : AddOrderDialog<Offset>(
    resourced,
    R.string.add_offset,
    R.image.ic_offset
) {
    private lateinit var machineChoice: ChoiceBox<OffsetPrice>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun _GridPane.onLayout() {
        label(getString(R.string.machine)) col 0 row 2
        machineChoice = choiceBox(transaction { OffsetPrices.find().toObservableList() }!!) {
            valueProperty().listener { _, _, offset ->
                minQtyField.value = offset.minQty
                minPriceField.value = offset.minPrice
                excessPriceField.value = offset.excessPrice
            }
        } col 1 row 2
        label(getString(R.string.min_qty)) col 0 row 3
        minQtyField = intField { promptText = getString(R.string.min_qty) } col 1 row 3
        label(getString(R.string.min_price)) col 0 row 4
        minPriceField = doubleField { promptText = getString(R.string.min_price) } col 1 row 4
        label(getString(R.string.excess_price)) col 0 row 5
        excessPriceField = doubleField { promptText = getString(R.string.excess_price) } col 1 row 5
    }

    override val titleBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty, minQtyField.valueProperty, minPriceField.valueProperty,
            excessPriceField.valueProperty)

    override val disableBinding: ObservableBooleanValue
        get() = machineChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty.lessEq(0) or
            minQtyField.valueProperty.lessEq(0) or
            minPriceField.valueProperty.lessEq(0) or
            excessPriceField.valueProperty.lessEq(0)

    override fun newInstance(): Offset = Invoices.Offsets.new(
        titleField.text,
        qtyField.value,
        machineChoice.value?.name ?: "",
        minQtyField.value,
        minPriceField.value,
        excessPriceField.value)
}