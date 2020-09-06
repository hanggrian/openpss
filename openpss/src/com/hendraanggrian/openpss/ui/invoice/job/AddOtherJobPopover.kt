package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.bindings.asBoolean
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.label

class AddOtherJobPopover(context: Context) :
    AddJobPopover<Invoice.OtherJob>(context, R.string.add_other_job),
    Invoice.Job {

    private lateinit var priceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R.string.price)).grid(currentRow, 0)
        priceField = addChild(DoubleField().apply { promptText = getString(R.string.price) }).grid(currentRow, 1 to 2)
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = titleField.textProperty().asBoolean { it.isNullOrBlank() } or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.OtherJob?
        get() = Invoice.OtherJob.new(qty, desc, total)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}
