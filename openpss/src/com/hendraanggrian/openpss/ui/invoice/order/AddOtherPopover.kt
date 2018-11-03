package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddOtherPopover(context: Context) : AddOrderPopover<Invoice.Other>(context, R.string.add_other),
    Invoice.Order {

    private lateinit var priceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.price)) col 0 row currentRow
        priceField = doubleField { promptText = getString(R.string.price) } col 1 colSpans 2 row currentRow
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.Other?
        get() = Invoice.Other.new(qty, title, total)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}