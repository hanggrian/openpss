package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.PlatePrice
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.isBlank
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.addNode
import ktfx.layouts.label

class AddPlateJobPopOver(component: FxComponent) :
    AddJobPopOver<Invoice.PlateJob>(component, R2.string.add_plate_job),
    Invoice.Job {

    private lateinit var typeChoice: ComboBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R2.string.type)) {
            gridAt(currentRow, 0)
        }
        typeChoice = jfxComboBox(runBlocking(Dispatchers.IO) { OpenPSSApi.getPlatePrices() }.toObservableList()) {
            gridAt(currentRow, 1)
            colSpans = 2
            valueProperty().listener { _, _, job ->
                priceField.value = job.price
            }
        }
        currentRow++
        label(getString(R2.string.price)) {
            gridAt(currentRow, 0)
        }
        priceField = addNode(DoubleField()) {
            gridAt(currentRow, 1)
            colSpans = 2
            promptText = getString(R2.string.price)
        }
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.PlateJob?
        get() = Invoice.PlateJob.new(qty, desc, total, typeChoice.value.name)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}
