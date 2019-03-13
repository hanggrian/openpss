package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.PlatePrice
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
import ktfx.jfoenix.jfxComboBox
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddPlateJobPopOver(component: FxComponent) :
    AddJobPopOver<Invoice.PlateJob>(component, R2.string.add_plate_job),
    Invoice.Job {

    private lateinit var typeChoice: ComboBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R2.string.type)) col 0 row currentRow
        typeChoice =
            jfxComboBox(runBlocking(Dispatchers.IO) { api.getPlatePrices() }.toObservableList()) {
                valueProperty().listener { _, _, job ->
                    priceField.value = job.price
                }
            } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R2.string.price)) col 0 row currentRow
        priceField = doubleField {
            promptText = getString(R2.string.price)
        } col 1 colSpans 2 row currentRow
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