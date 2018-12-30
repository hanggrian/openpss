package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.data.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.bindings.isBlank
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddOtherJobPopOver(component: FxComponent) : AddJobPopOver<Invoice.OtherJob>(component, R.string.add_other_job),
    Invoice.Job {

    private lateinit var priceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.price)) col 0 row currentRow
        priceField = DoubleField().apply { promptText = getString(R.string.price) }() col 1 colSpans 2 row currentRow
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.OtherJob?
        get() = Invoice.OtherJob.new(qty, desc, total)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}