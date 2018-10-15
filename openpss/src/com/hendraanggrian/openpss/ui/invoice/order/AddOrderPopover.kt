package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.image.ImageView
import ktfx.beans.binding.stringBindingOf
import ktfx.coroutines.listener
import ktfx.layouts._GridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.layouts.toggleButton
import ktfx.layouts.tooltip
import ktfx.scene.layout.gap

abstract class AddOrderPopover<T : Titled>(resourced: Resourced, titleId: String) :
    ResultablePopover<T>(resourced, titleId), Invoice.Order {

    abstract fun _GridPane.onCreateContent()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    abstract fun calculateTotal(): Double

    protected var currentRow: Int = 0
    protected lateinit var titleField: TextField
    protected lateinit var qtyField: IntField
    protected lateinit var totalField: DoubleField
    protected lateinit var customizeToggle: ToggleButton

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.title)) col 0 row currentRow
            titleField = textField { promptText = getString(R.string.title) } col 1 colSpans 2 row currentRow
            currentRow++
            label(getString(R.string.qty)) col 0 row currentRow
            qtyField = intField { promptText = getString(R.string.qty) } col 1 colSpans 2 row currentRow
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R.string.total)) col 0 row currentRow
            totalField = doubleField { bindTotal() } col 1 row currentRow
            customizeToggle = toggleButton(graphic = ImageView(R.image.btn_edit_light)) {
                tooltip(getString(R.string.customize_total))
                selectedProperty().listener { _, _, selected ->
                    when {
                        selected -> {
                            val s = totalField.text
                            totalField.textProperty().unbind()
                            totalField.text = s
                        }
                        else -> totalField.bindTotal()
                    }
                }
            } col 2 row currentRow
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultButtonDisableBinding)
        }
    }

    override val qty: Int get() = qtyField.value

    override val total: Double get() = totalField.value

    private fun DoubleField.bindTotal() = textProperty().bind(stringBindingOf(*totalBindingDependencies) {
        calculateTotal().toString()
    })
}