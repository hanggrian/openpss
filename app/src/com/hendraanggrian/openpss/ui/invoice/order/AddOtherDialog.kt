package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Other
import com.hendraanggrian.openpss.controls.DoubleField
import com.hendraanggrian.openpss.controls.doubleField
import com.hendraanggrian.openpss.ui.Resourced
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
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

    override val titleBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty, priceField.valueProperty)

    override val disableBinding: ObservableBooleanValue
        get() = titleField.textProperty().isBlank() or
            qtyField.valueProperty.lessEq(0) or
            priceField.valueProperty.lessEq(0)

    override fun newInstance(): Other = Other.new(
        titleField.text,
        qtyField.value,
        priceField.value)
}