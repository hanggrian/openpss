package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.utils.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Offset
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.ui.Resourced
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableStringValue
import javafx.scene.control.ChoiceBox
import ktfx.beans.binding.stringBindingOf
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
    private lateinit var typeChoice: ChoiceBox<OffsetPrice>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun _GridPane.onLayout() {
        label(getString(R.string.type)) col 0 row 2
        typeChoice = choiceBox(transaction { OffsetPrices.find().toObservableList() }!!) {
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

    override val titleBinding: ObservableStringValue
        get() = stringBindingOf(qtyField.valueProperty, minQtyField.valueProperty, minPriceField.valueProperty,
            excessPriceField.valueProperty) {
            currencyConverter.toString(Offset.calculateTotal(qtyField.value, minQtyField.value, minPriceField.value,
                excessPriceField.value))
        }

    override val disableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty.lessEq(0) or
            minQtyField.valueProperty.lessEq(0) or
            minPriceField.valueProperty.lessEq(0) or
            excessPriceField.valueProperty.lessEq(0)

    override fun newInstance(): Offset = Offset.new(
        titleField.text,
        qtyField.value,
        typeChoice.value.name,
        minQtyField.value,
        minPriceField.value,
        excessPriceField.value)
}