package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import ktfx.bindings.asBoolean
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.label

class AddDigitalJobPopover(context: Context) :
    AddJobPopover<Invoice.DigitalJob>(context, R.string.add_digital_job), Invoice.Job {

    private lateinit var typeChoice: ComboBox<DigitalPrice>
    private lateinit var twoSideCheck: CheckBox
    private lateinit var oneSidePriceField: DoubleField
    private lateinit var twoSidePriceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R.string.type)).grid(currentRow, 0)
        typeChoice = jfxComboBox(transaction { DigitalPrices().toObservableList() }) {
            valueProperty().listener { _, _, job ->
                oneSidePriceField.value = job.oneSidePrice
                twoSidePriceField.value = job.twoSidePrice
            }
        }.grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.two_side)).grid(currentRow, 0)
        twoSideCheck = jfxCheckBox().grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.one_side_price)).grid(currentRow, 0)
        oneSidePriceField = addChild(
            DoubleField().apply {
                promptText = getString(R.string.one_side_price)
            }
        ).grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.two_side_price)).grid(currentRow, 0)
        twoSidePriceField = addChild(
            DoubleField().apply {
                promptText = getString(R.string.two_side_price)
            }.grid(currentRow, 1 to 2)
        )
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
            titleField.textProperty().asBoolean { it.isNullOrBlank() } or
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
