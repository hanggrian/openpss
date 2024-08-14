package com.hanggrian.openpss.ui.invoice.job

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DoubleField
import com.hanggrian.openpss.db.schemas.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.label

class AddOtherJobPopover(context: Context) :
    AddJobPopover<Invoice.OtherJob>(context, R.string_add_other_job),
    Invoice.Job {
    private lateinit var priceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R.string_price))
            .grid(currentRow, 0)
        priceField =
            addChild(DoubleField().apply { promptText = getString(R.string_price) })
                .grid(currentRow, 1 to 2)
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty, priceField.valueProperty)

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() =
            titleField.textProperty().booleanBindingBy { it.isNullOrBlank() } or
                qtyField.valueProperty.lessEq(0) or
                totalField.valueProperty.lessEq(0)

    override val nullableResult: Invoice.OtherJob
        get() = Invoice.OtherJob.new(qty, desc, total)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}
