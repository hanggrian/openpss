package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.Payment
import com.hendraanggrian.openpss.ui.ResultablePopOver
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.booleanBindingOf
import ktfx.controls.gap
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tooltip
import ktfx.text.invoke
import ktfx.toBinding
import ktfx.toStringBinding

class AddPaymentPopOver(
    component: FxComponent,
    private val invoice: Invoice
) : ResultablePopOver<Payment>(component, R2.string.add_payment) {

    private val valueField: DoubleField
    private val cashBox: CheckBox
    private val referenceField: TextField
    private val receivable = invoice.total - runBlocking(Dispatchers.IO) { OpenPSSApi.getPaymentDue(invoice.id) }

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.employee)) row 0 col 0
            label(login.name) { styleClass += R.style.bold } row 0 col (1 to 2)
            label(getString(R2.string.receivable)) row 1 col 0
            label(currencyConverter(receivable)) { styleClass += R.style.bold } row 1 col (1 to 2)
            label(getString(R2.string.payment)) row 2 col 0
            valueField = addChild(DoubleField().apply { promptText = getString(R2.string.payment) }) row 2 col 1
            jfxButton(graphic = ImageView(R.image.btn_match_receivable)) {
                styleClass += R.style.flat
                tooltip(getString(R2.string.match_receivable))
                onAction {
                    valueField.value = receivable
                }
            } row 2 col 2
            label(getString(R2.string.remaining)) row 3 col 0
            label {
                styleClass += R.style.bold
                textProperty().bind(valueField.valueProperty().toStringBinding {
                    (receivable - it).let { remaining ->
                        when {
                            remaining <= 0.0 -> getString(R2.string.paid)
                            else -> currencyConverter(remaining)
                        }
                    }
                })
                textFillProperty().bind(textProperty().toBinding {
                    getColor(
                        when {
                            receivable - valueField.value <= 0.0 -> R.value.color_green
                            else -> R.value.color_red
                        }
                    )
                })
            } row 3 col (1 to 2)
            label(getString(R2.string.cash)) row 6 col 0
            cashBox = jfxCheckBox { isSelected = true } row 6 col (1 to 2)
            label(getString(R2.string.reference)) { bindDisable() } row 7 col 0
            referenceField = jfxTextField { bindDisable() } row 7 col (1 to 2)
        }
        defaultButton.disableProperty().bind(booleanBindingOf(
            valueField.valueProperty(), cashBox.selectedProperty(),
            referenceField.textProperty()
        ) {
            (!valueField.isValid || valueField.value <= 0 || valueField.value > receivable).let {
                when {
                    cashBox.isSelected -> it
                    else -> it || referenceField.text.isBlank()
                }
            }
        })
    }

    override val nullableResult: Payment?
        get() = Payment.new(
            invoice.id,
            login.id,
            runBlocking(Dispatchers.IO) { OpenPSSApi.getDateTime() },
            valueField.value,
            when {
                cashBox.isSelected -> null
                else -> referenceField.text
            }
        )

    private fun Node.bindDisable() = disableProperty().bind(cashBox.selectedProperty())
}
