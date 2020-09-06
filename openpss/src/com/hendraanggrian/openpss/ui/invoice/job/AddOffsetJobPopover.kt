package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
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
import ktfx.text.buildStringConverter

class AddOffsetJobPopover(context: Context) :
    AddJobPopover<Invoice.OffsetJob>(context, R.string.add_offset_job), Invoice.Job {

    private lateinit var typeChoice: ComboBox<OffsetPrice>
    private lateinit var techniqueChoice: ComboBox<Invoice.OffsetJob.Technique>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R.string.type)).grid(currentRow, 0)
        typeChoice = jfxComboBox(transaction { OffsetPrices().toObservableList() }) {
            valueProperty().listener { _, _, job ->
                minQtyField.value = job.minQty
                minPriceField.value = job.minPrice
                excessPriceField.value = job.excessPrice
            }
        }.grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.technique)).grid(currentRow, 0)
        techniqueChoice = jfxComboBox(Invoice.OffsetJob.Technique.values().toObservableList()) {
            converter = buildStringConverter { toString { it!!.toString(this@AddOffsetJobPopover) } }
            selectionModel.selectFirst()
        }.grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.min_qty)).grid(currentRow, 0)
        minQtyField = addChild(
            IntField().apply { promptText = getString(R.string.min_qty) }
        ).grid(currentRow, 1 to 2)
        currentRow++
        label(getString(R.string.min_price)).grid(currentRow, 0)
        minPriceField = addChild(
            DoubleField().apply { promptText = getString(R.string.min_price) }
        ).grid(currentRow, 1 to 2)
        currentRow
        currentRow++
        label(getString(R.string.excess_price)).grid(currentRow, 0)
        excessPriceField = addChild(
            DoubleField().apply { promptText = getString(R.string.excess_price) }
        ).grid(currentRow, 1 to 2)
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(
            qtyField.valueProperty(),
            techniqueChoice.valueProperty(),
            minQtyField.valueProperty(),
            minPriceField.valueProperty(),
            excessPriceField.valueProperty()
        )

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().asBoolean { it.isNullOrBlank() } or
            qtyField.valueProperty().lessEq(0) or
            techniqueChoice.valueProperty().isNull or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.OffsetJob?
        get() = Invoice.OffsetJob.new(qty, desc, total, typeChoice.value.name, techniqueChoice.value)

    override fun calculateTotal(): Double = when (techniqueChoice.value) {
        null -> 0.0
        Invoice.OffsetJob.Technique.ONE_SIDE -> calculateSide(
            qty,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Invoice.OffsetJob.Technique.TWO_SIDE_EQUAL -> calculateSide(
            qty * 2,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Invoice.OffsetJob.Technique.TWO_SIDE_DISTINCT -> calculateSide(
            qty,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        ) * 2
    }

    private fun calculateSide(qty: Int, minQty: Int, minPrice: Double, excessPrice: Double) = when {
        qty <= minQty -> minPrice
        else -> minPrice + ((qty - minQty) * excessPrice)
    }
}
