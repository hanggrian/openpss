package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.PaymentMethod
import com.hendraanggrian.openpss.db.schemas.PaymentMethod.CASH
import com.hendraanggrian.openpss.db.schemas.PaymentMethod.values
import com.hendraanggrian.openpss.db.schemas.calculateDue
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.getColor
import com.hendraanggrian.openpss.utils.getFont
import javafx.scene.Node
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.value.eq
import ktfx.beans.value.greater
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.layouts.tooltip
import ktfx.listeners.converter
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class AddPaymentDialog(controller: Controller, invoice: Invoice) : Dialog<Payment>(), Resourced by controller {

    private lateinit var valueField: DoubleField
    private lateinit var methodChoice: ChoiceBox<PaymentMethod>
    private lateinit var transferField: TextField
    private val receivable = transaction { calculateDue(invoice) }!!

    init {
        headerTitle = getString(R.string.add_payment)
        graphicIcon = ImageView(R.image.ic_payment)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.employee)) row 0 col 0
            label(controller.employeeName) {
                font = getFont(R.font.opensans_bold)
            } row 0 col 1 colSpans 2
            label(getString(R.string.receivable)) row 1 col 0
            label(currencyConverter.toString(receivable)) {
                font = getFont(R.font.opensans_bold)
            } row 1 col 1 colSpans 2
            label(getString(R.string.payment)) row 2 col 0
            valueField = doubleField { promptText = getString(R.string.payment) } row 2 col 1
            button(graphic = ImageView(R.image.btn_match_receivable)) {
                tooltip(getString(R.string.match_receivable))
                onAction { valueField.value = receivable }
            } row 2 col 2
            label(getString(R.string.remaining)) row 3 col 0
            label {
                font = getFont(R.font.opensans_bold)
                textProperty().bind(stringBindingOf(valueField.valueProperty) {
                    (receivable - valueField.value).let { remaining ->
                        when (remaining) {
                            0.0 -> getString(R.string.paid)
                            else -> currencyConverter.toString(remaining)
                        }
                    }
                })
                textFillProperty().bind(bindingOf(valueField.valueProperty) {
                    getColor(when {
                        receivable - valueField.value == 0.0 -> R.color.teal
                        else -> R.color.red
                    })
                })
            } row 3 col 1 colSpans 2
            label(getString(R.string.payment_method)) row 6 col 0
            methodChoice = choiceBox(values().toObservableList()) {
                converter { toString { it!!.getDisplayText(this@AddPaymentDialog) } }
            } row 6 col 1 colSpans 2
            label(getString(R.string.transfer_reference)) { bindDisable() } row 7 col 0
            transferField = textField { bindDisable() } row 7 col 1 colSpans 2
        }
        valueField.requestFocus()
        cancelButton()
        okButton {
            val binding = !valueField.validProperty or
                valueField.valueProperty.lessEq(0) or
                valueField.valueProperty.greater(receivable)
            methodChoice.selectionModel.selectedItemProperty().listener { _, _, method ->
                disableProperty().bind(when (method) {
                    CASH -> binding
                    else -> binding or transferField.textProperty().isBlank()
                })
            }
        }
        methodChoice.selectionModel.selectFirst()
        setResultConverter {
            when (it) {
                CANCEL -> null
                else -> Payment.new(invoice.id, controller.employeeId, valueField.value,
                    when (methodChoice.selectionModel.selectedItem) {
                        CASH -> null
                        else -> transferField.text
                    })
            }
        }
    }

    private fun Node.bindDisable() = disableProperty().bind(methodChoice.selectionModel.selectedItemProperty() eq CASH)
}