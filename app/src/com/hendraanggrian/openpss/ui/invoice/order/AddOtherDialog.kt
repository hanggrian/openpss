package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Other
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.ui.Resourced
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableStringValue
import ktfx.beans.binding.lessEq
import ktfx.beans.binding.or
import ktfx.beans.binding.stringBindingOf
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddOtherDialog(resourced: Resourced) : AddOrderDialog<Other>(
    resourced,
    R.string.add_other
) {
    private lateinit var priceField: DoubleField

    override fun _GridPane.onLayout() {
        label(getString(R.string.price)) col 0 row 2
        priceField = doubleField { promptText = getString(R.string.price) } col 1 row 2
    }

    override val titleBinding: ObservableStringValue
        get() = stringBindingOf(qtyField.valueProperty, priceField.valueProperty) {
            currencyConverter.toString(qtyField.value * priceField.value)
        }

    override val disableBinding: ObservableBooleanValue
        get() = titleField.textProperty().isEmpty or
            qtyField.valueProperty.lessEq(0) or
            priceField.valueProperty.lessEq(0)

    override fun newInstance(): Other = Other.new(
        titleField.text,
        qtyField.value,
        priceField.value)
}