package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.data.Payment
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.ui.ResultablePopover
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotlinx.coroutines.runBlocking
import ktfx.bindings.buildBinding
import ktfx.bindings.buildBooleanBinding
import ktfx.bindings.buildStringBinding
import ktfx.controls.gap
import ktfx.coroutines.onAction
import ktfx.invoke
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxCheckBox
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tooltip

class AddPaymentPopover(
    component: FxComponent,
    private val invoice: Invoice
) : ResultablePopover<Payment>(component, R.string.add_payment) {

    private lateinit var valueField: DoubleField
    private lateinit var cashBox: CheckBox
    private lateinit var referenceField: TextField
    private val receivable = invoice.total - runBlocking { api.getPaymentDue(invoice.id) }

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R.string.employee)) row 0 col 0
            label(login.name) {
                styleClass += R.style.bold
            } row 0 col 1 colSpans 2
            label(getString(R.string.receivable)) row 1 col 0
            label(currencyConverter(receivable)) {
                styleClass += R.style.bold
            } row 1 col 1 colSpans 2
            label(getString(R.string.payment)) row 2 col 0
            valueField = DoubleField().apply { promptText = getString(R.string.payment) }() row
                2 col 1
            jfxButton(graphic = ImageView(R.image.btn_match_receivable)) {
                styleClass += R.style.flat
                tooltip(getString(R.string.match_receivable))
                onAction {
                    valueField.value = receivable
                }
            } row 2 col 2
            label(getString(R.string.remaining)) row 3 col 0
            label {
                styleClass += R.style.bold
                textProperty().bind(buildStringBinding(valueField.valueProperty()) {
                    (receivable - valueField.value).let { remaining ->
                        when {
                            remaining <= 0.0 -> getString(R.string.paid)
                            else -> currencyConverter(remaining)
                        }
                    }
                })
                textFillProperty().bind(buildBinding(textProperty()) {
                    getColor(
                        when {
                            receivable - valueField.value <= 0.0 -> R.value.color_green
                            else -> R.value.color_red
                        }
                    )
                })
            } row 3 col 1 colSpans 2
            label(getString(R.string.cash)) row 6 col 0
            cashBox = jfxCheckBox { isSelected = true } row 6 col 1 colSpans 2
            label(getString(R.string.reference)) { bindDisable() } row 7 col 0
            referenceField = jfxTextField { bindDisable() } row 7 col 1 colSpans 2
        }
        defaultButton.disableProperty().bind(buildBooleanBinding(
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
            invoice.id, login.id, runBlocking { api.getDateTime() }, valueField.value,
            when {
                cashBox.isSelected -> null
                else -> referenceField.text
            }
        )

    private fun Node.bindDisable() = disableProperty().bind(cashBox.selectedProperty())
}