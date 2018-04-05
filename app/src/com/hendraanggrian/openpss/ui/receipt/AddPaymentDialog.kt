package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schema.Payment
import com.hendraanggrian.openpss.db.schema.PaymentMethod
import com.hendraanggrian.openpss.db.schema.PaymentMethod.CASH
import com.hendraanggrian.openpss.db.schema.PaymentMethod.values
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.db.schema.calculateDue
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.getResourceString
import javafx.scene.Node
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.text.Font.loadFont
import ktfx.beans.binding.eq
import ktfx.beans.binding.greater
import ktfx.beans.binding.lessEq
import ktfx.beans.binding.or
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

class AddPaymentDialog(controller: Controller, receipt: Receipt) : Dialog<Payment>(), Resourced by controller {

    private lateinit var valueField: DoubleField
    private lateinit var methodChoice: ChoiceBox<PaymentMethod>
    private lateinit var transferField: TextField
    private val remaining = transaction { calculateDue(receipt) }!!

    init {
        headerTitle = getString(R.string.add_payment)
        graphicIcon = ImageView(R.image.ic_payment)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.employee)) row 0 col 0
            label(controller.employeeName) {
                font = loadFont(getResourceString(R.font.opensans_bold), 13.0)
            } row 0 col 1 colStretch 2
            label(getString(R.string.remaining)) row 1 col 0
            label(currencyConverter.toString(remaining)) {
                font = loadFont(getResourceString(R.font.opensans_bold), 13.0)
            } row 1 col 1 colStretch 2
            label(getString(R.string.value)) row 2 col 0
            valueField = doubleField { promptText = getString(R.string.payment) } row 2 col 1
            button(graphic = ImageView(R.image.btn_match_remaining)) {
                tooltip(getString(R.string.match_remaining))
                onAction { valueField.value = remaining }
            } row 2 col 2
            label(getString(R.string.payment_method)) row 3 col 0
            methodChoice = choiceBox(values().toObservableList()) {
                converter { toString { it!!.getDisplayText(this@AddPaymentDialog) } }
            } row 3 col 1 colStretch 2
            label(getString(R.string.transfer_id)) { bindDisable() } row 4 col 0
            transferField = textField { bindDisable() } row 4 col 1 colStretch 2
        }
        cancelButton()
        okButton {
            val binding = !valueField.validProperty or
                valueField.valueProperty.lessEq(0) or
                valueField.valueProperty.greater(remaining)
            methodChoice.selectionModel.selectedItemProperty().listener { _, _, method ->
                disableProperty().bind(when (method) {
                    CASH -> binding
                    else -> binding or transferField.textProperty().isEmpty
                })
            }
        }
        methodChoice.selectionModel.selectFirst()
        setResultConverter {
            when (it) {
                CANCEL -> null
                else -> Payment.new(receipt.id, controller.employeeId, valueField.value,
                    when (methodChoice.selectionModel.selectedItem) {
                        CASH -> null
                        else -> transferField.text
                    })
            }
        }
    }

    private fun Node.bindDisable() = disableProperty().bind(methodChoice.selectionModel.selectedItemProperty() eq CASH)
}