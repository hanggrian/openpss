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
        label(getString(R2.string.type)) {
            gridAt(currentRow, 0)
        }
        typeChoice = jfxComboBox(runBlocking(Dispatchers.IO) { OpenPSSApi.getDigitalPrices() }.toObservableList()) {
            gridAt(currentRow, 1)
            colSpans = 2
            valueProperty().listener { _, _, job ->
                oneSidePriceField.value = job.oneSidePrice
                twoSidePriceField.value = job.twoSidePrice
            }
        }
        currentRow++
        label(getString(R2.string.two_side)) {
            gridAt(currentRow, 0)
        }
        twoSideCheck = jfxCheckBox {
            gridAt(currentRow, 1)
            colSpans = 2
        }
        currentRow++
        label(getString(R2.string.one_side_price)) {
            gridAt(currentRow, 0)
        }
        oneSidePriceField = addNode(DoubleField()) {
            gridAt(currentRow, 1)
            colSpans = 2
            promptText = getString(R2.string.one_side_price)
        }
        currentRow++
        label(getString(R2.string.two_side_price)) {
            gridAt(currentRow, 0)
        }
        twoSidePriceField = addNode(DoubleField()) {
            gridAt(currentRow, 1)
            colSpans = 2
            promptText = getString(R2.string.two_side_price)
        }
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
