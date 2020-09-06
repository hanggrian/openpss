package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox
import ktfx.bindings.asBoolean
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.label

class AddPlateJobPopover(context: Context) :
    AddJobPopover<Invoice.PlateJob>(context, R.string.add_plate_job),
    Invoice.Job {

    private lateinit var typeChoice: ComboBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R.string.type)).grid(currentRow, 0)
        typeChoice = jfxComboBox(transaction { PlatePrices().toObservableList() }) {
            valueProperty().listener { _, _, job ->
                priceField.value = job.price
            }
        }.grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.price)).grid(currentRow, 0)
        priceField = addChild(DoubleField().apply { promptText = getString(R.string.price) }).grid(currentRow, 1 to 2)
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().asBoolean { it.isNullOrBlank() } or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.PlateJob?
        get() = Invoice.PlateJob.new(qty, desc, total, typeChoice.value.name)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}
