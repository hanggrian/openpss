package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.db.SimpleOrder
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.localization.Resourced
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddOtherPopover(resourced: Resourced) : AddOrderPopover<Invoice.Other>(
    resourced,
    R.string.add_other
), SimpleOrder {
    private lateinit var priceField: DoubleField

    override fun _GridPane.onLayout() {
        label(getString(R.string.price)) col 0 row 2
        priceField = doubleField { promptText = getString(R.string.price) } col 1 row 2
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val disableBinding: ObservableBooleanValue
        get() = titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            priceField.valueProperty().lessEq(0)

    override fun newInstance(): Invoice.Other = Invoice.Other.new(
        titleField.text,
        qtyField.value,
        priceField.value)

    override val qty: Int get() = qtyField.value

    override val price: Double get() = priceField.value
}