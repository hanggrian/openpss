package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.schema.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.bindings.isBlank
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.label

class AddOtherJobPopOver(component: FxComponent) : AddJobPopOver<Invoice.OtherJob>(
    component,
    R2.string.add_other_job
), Invoice.Job {

    private lateinit var priceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R2.string.price)) col 0 row currentRow
        priceField = addNode(DoubleField().apply {
            promptText = getString(R2.string.price)
        }) col 1 colSpans 2 row currentRow
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
