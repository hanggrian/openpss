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
import ktfx.bindings.bindingOf
import ktfx.bindings.booleanBindingOf
import ktfx.bindings.stringBindingOf
import ktfx.coroutines.onAction
import ktfx.invoke
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.addNode
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tooltip

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
            label(getString(R2.string.employee)) {
                gridAt(0, 0)
            }
            label(login.name) {
                gridAt(0, 1, colSpans = 2)
                styleClass += R.style.bold
            }
            label(getString(R2.string.receivable)) {
                gridAt(1, 0)
            }
            label(currencyConverter(receivable)) {
                gridAt(1, 1, colSpans = 2)
                styleClass += R.style.bold
            }
            label(getString(R2.string.payment)) {
                gridAt(2, 0)
            }
            valueField = addNode(DoubleField()) {
                gridAt(2, 1)
                promptText = getString(R2.string.payment)
            }
            jfxButton(graphic = ImageView(R.image.btn_match_receivable)) {
                gridAt(2, 2)
                styleClass += R.style.flat
                tooltip(getString(R2.string.match_receivable))
                onAction {
                    valueField.value = receivable
                }
            }
            label(getString(R2.string.remaining)) {
                gridAt(3, 0)
            }
            label {
                gridAt(3, 1, colSpans = 2)
                styleClass += R.style.bold
                textProperty().bind(stringBindingOf(valueField.valueProperty()) {
                    (receivable - valueField.value).let { remaining ->
                        when {
                            remaining <= 0.0 -> getString(R2.string.paid)
                            else -> currencyConverter(remaining)
                        }
                    }
                })
                textFillProperty().bind(bindingOf(textProperty()) {
                    getColor(
                        when {
                            receivable - valueField.value <= 0.0 -> R.value.color_green
                            else -> R.value.color_red
                        }
                    )
                })
            }
            label(getString(R2.string.cash)) {
                gridAt(6, 0)
            }
            cashBox = jfxCheckBox {
                gridAt(6, 1, colSpans = 2)
                isSelected = true
            }
            label(getString(R2.string.reference)) {
                gridAt(7, 0)
                bindDisable()
            }
            referenceField = jfxTextField {
                gridAt(7, 1, colSpans = 2)
                bindDisable()
            }
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
