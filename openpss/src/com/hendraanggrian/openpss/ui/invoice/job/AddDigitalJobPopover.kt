package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.JFXDoubleField
import com.hendraanggrian.openpss.control.jfxDoubleField
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxCheckBox
import ktfx.jfoenix.jfxComboBox
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddDigitalJobPopover(context: Context) :
    AddJobPopover<Invoice.DigitalJob>(context, R.string.add_digital_job), Invoice.Job {

    private lateinit var typeChoice: ComboBox<DigitalPrice>
    private lateinit var twoSideCheck: CheckBox
    private lateinit var oneSidePriceField: JFXDoubleField
    private lateinit var twoSidePriceField: JFXDoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.type)) col 0 row currentRow
        typeChoice = jfxComboBox(transaction { DigitalPrices().toObservableList() }) {
            valueProperty().listener { _, _, job ->
                oneSidePriceField.value = job.oneSidePrice
                twoSidePriceField.value = job.twoSidePrice
            }
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.two_side)) col 0 row currentRow
        twoSideCheck = jfxCheckBox() col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.one_side_price)) col 0 row currentRow
        oneSidePriceField = jfxDoubleField {
            promptText = getString(R.string.one_side_price)
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.two_side_price)) col 0 row currentRow
        twoSidePriceField = jfxDoubleField {
            promptText = getString(R.string.two_side_price)
        } col 1 colSpans 2 row currentRow
        currentRow++
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(
            qtyField.valueProperty(),
            twoSideCheck.selectedProperty(),
            oneSidePriceField.valueProperty(),
            twoSidePriceField.valueProperty()
        )

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            oneSidePriceField.valueProperty().lessEq(0) or
            twoSidePriceField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.DigitalJob?
        get() = Invoice.DigitalJob.new(qty, desc, total, typeChoice.value.name, twoSideCheck.isSelected)

    override fun calculateTotal(): Double = qtyField.value * when {
        twoSideCheck.isSelected -> twoSidePriceField.value
        else -> oneSidePriceField.value
    }
}