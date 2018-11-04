package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxComboBox
import ktfx.layouts._GridPane
import ktfx.layouts.label

class AddPlateJobPopover(context: Context) : AddJobPopover<Invoice.PlateJob>(context, R.string.add_plate_job),
    Invoice.Job {

    private lateinit var typeChoice: ComboBox<PlatePrice>
    private lateinit var priceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.machine)) col 0 row currentRow
        typeChoice = jfxComboBox(transaction { PlatePrices().toObservableList() }) {
            valueProperty().listener { _, _, job ->
                priceField.value = job.price
            }
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.price)) col 0 row currentRow
        priceField = doubleField { promptText = getString(R.string.price) } col 1 colSpans 2 row currentRow
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(qtyField.valueProperty(), priceField.valueProperty())

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.PlateJob?
        get() = Invoice.PlateJob.new(qty, title, total, typeChoice.value.name)

    override fun calculateTotal(): Double = qtyField.value * priceField.value
}