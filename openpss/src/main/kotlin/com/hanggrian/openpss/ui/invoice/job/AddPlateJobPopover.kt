package com.hanggrian.openpss.ui.invoice.job

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DoubleField
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.PlatePrice
import com.hanggrian.openpss.db.schemas.PlatePrices
import com.hanggrian.openpss.db.transaction
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.label

class AddPlateJobPopover(context: Context) :
    AddJobPopover<Invoice.PlateJob>(context, R.string_add_plate_job),
    Invoice.Job {
    private lateinit var typeChoice: ComboBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R.string_type))
            .grid(currentRow, 0)
        typeChoice =
            jfxComboBox(transaction { PlatePrices().toObservableList() }) {
                valueProperty().listener { _, _, job ->
                    priceField.value = job.price
                }
            }.grid(currentRow, 1 to 2)
        currentRow++
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
            typeChoice.valueProperty().isNull or
                titleField.textProperty().booleanBindingBy { it.isNullOrBlank() } or
                qtyField.valueProperty.lessEq(0) or
                totalField.valueProperty.lessEq(0)

    override val nullableResult: Invoice.PlateJob
        get() = Invoice.PlateJob.new(qty, desc, total, typeChoice.value.name)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}
