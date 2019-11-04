package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.schema.DigitalPrice
import com.hendraanggrian.openpss.schema.Invoice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.isBlank
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.addNode
import ktfx.layouts.label

class AddDigitalJobPopOver(component: FxComponent) :
    AddJobPopOver<Invoice.DigitalJob>(component, R2.string.add_digital_job), Invoice.Job {

    private lateinit var typeChoice: ComboBox<DigitalPrice>
    private lateinit var twoSideCheck: CheckBox
    private lateinit var oneSidePriceField: DoubleField
    private lateinit var twoSidePriceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R2.string.type)) col 0 row currentRow
        typeChoice =
            jfxComboBox(runBlocking(Dispatchers.IO) { OpenPSSApi.getDigitalPrices() }.toObservableList()) {
                valueProperty().listener { _, _, job ->
                    oneSidePriceField.value = job.oneSidePrice
                    twoSidePriceField.value = job.twoSidePrice
                }
            } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R2.string.two_side)) col 0 row currentRow
        twoSideCheck = jfxCheckBox() col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R2.string.one_side_price)) col 0 row currentRow
        oneSidePriceField = addNode(DoubleField()) {
            promptText = getString(R2.string.one_side_price)
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R2.string.two_side_price)) col 0 row currentRow
        twoSidePriceField = addNode(DoubleField()) {
            promptText = getString(R2.string.two_side_price)
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
        get() = Invoice.DigitalJob.new(
            qty,
            desc,
            total,
            typeChoice.value.name,
            twoSideCheck.isSelected
        )

    override fun calculateTotal(): Double = qtyField.value * when {
        twoSideCheck.isSelected -> twoSidePriceField.value
        else -> oneSidePriceField.value
    }
}
