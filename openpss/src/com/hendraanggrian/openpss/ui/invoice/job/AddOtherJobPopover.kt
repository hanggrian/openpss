package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.JFXDoubleField
import com.hendraanggrian.openpss.control.jfxDoubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddOtherJobPopover(context: Context) : AddJobPopover<Invoice.OtherJob>(context, R.string.add_other_job),
    Invoice.Job {

    private lateinit var priceField: JFXDoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.price)) col 0 row currentRow
        priceField = jfxDoubleField { promptText = getString(R.string.price) } col 1 colSpans 2 row currentRow
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.OtherJob?
        get() = Invoice.OtherJob.new(qty, title, total)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}