package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Offset
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.DoubleField
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.doubleField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.lessEq
import ktfx.beans.binding.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class AddOffsetDialog(resourced: Resourced) : Dialog<Offset>(), Resourced by resourced {

    private lateinit var typeChoice: ChoiceBox<OffsetPrice>
    private lateinit var titleField: TextField
    private lateinit var qtyField: IntField
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    init {
        headerTitle = getString(R.string.add_offset)
        graphicIcon = ImageView(R.image.ic_offset)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.type)) col 0 row 0
            typeChoice = choiceBox(transaction { OffsetPrices.find().toObservableList() }!!) {
                valueProperty().listener { _, _, offset ->
                    minQtyField.value = offset.minQty
                    minPriceField.value = offset.minPrice
                    excessPriceField.value = offset.excessPrice
                }
            } col 1 row 0
            label(getString(R.string.title)) col 0 row 1
            titleField = textField { promptText = getString(R.string.title) } col 1 row 1
            label(getString(R.string.qty)) col 0 row 2
            qtyField = intField { promptText = getString(R.string.qty) } col 1 row 2
            label(getString(R.string.min_qty)) col 0 row 3
            minQtyField = intField { promptText = getString(R.string.min_qty) } col 1 row 3
            label(getString(R.string.min_price)) col 0 row 4
            minPriceField = doubleField { promptText = getString(R.string.min_price) } col 1 row 4
            label(getString(R.string.excess_price)) col 0 row 5
            excessPriceField = doubleField { promptText = getString(R.string.excess_price) } col 1 row 5
        }
        cancelButton()
        okButton {
            disableProperty().bind(typeChoice.valueProperty().isNull or
                titleField.textProperty().isEmpty or
                qtyField.valueProperty.lessEq(0) or
                minQtyField.valueProperty.lessEq(0) or
                minPriceField.valueProperty.lessEq(0) or
                excessPriceField.valueProperty.lessEq(0))
        }
        setResultConverter {
            if (it == CANCEL) null else Offset.new(
                typeChoice.value.name,
                titleField.text,
                qtyField.value,
                minQtyField.value,
                minPriceField.value,
                excessPriceField.value)
        }
    }
}