package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Other
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import ktfx.beans.binding.lessEq
import ktfx.beans.binding.or
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gaps

class AddOtherDialog(resourced: Resourced) : Dialog<Other>(), Resourced by resourced {

    private lateinit var titleField: TextField
    private lateinit var qtyField: IntField
    private lateinit var priceField: DoubleField

    init {
        headerTitle = getString(R.string.add_other)
        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.title)) col 0 row 0
            titleField = textField { promptText = getString(R.string.title) } col 1 row 0
            label(getString(R.string.qty)) col 0 row 1
            qtyField = intField { promptText = getString(R.string.qty) } col 1 row 1
            label(getString(R.string.price)) col 0 row 2
            priceField = doubleField { promptText = getString(R.string.price) } col 1 row 2
        }
        cancelButton()
        okButton {
            disableProperty().bind(titleField.textProperty().isEmpty or
                qtyField.valueProperty.lessEq(0) or
                priceField.valueProperty.lessEq(0))
        }
        setResultConverter {
            if (it == CANCEL) null else Other.new(
                titleField.text,
                qtyField.value,
                priceField.value)
        }
    }
}