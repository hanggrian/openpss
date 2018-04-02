package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class AddPaymentDialog(controller: Controller) : Dialog<Double>(), Resourced by controller {

    private lateinit var doubleField: DoubleField

    init {
        headerTitle = getString(R.string.add_payment)
        graphicIcon = ImageView(R.image.ic_payment)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.employee)) row 0 col 0
            label(controller.employeeName) row 0 col 1
            label(getString(R.string.value)) row 1 col 0
            doubleField = doubleField { promptText = getString(R.string.payment) } row 1 col 1
        }
        cancelButton()
        okButton { disableProperty().bind(!doubleField.validProperty) }
        setResultConverter { if (it == CANCEL) null else doubleField.value }
    }
}