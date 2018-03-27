package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.PlatePrice
import com.hendraanggrian.openpss.db.schema.PlatePrices
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
import ktfx.scene.layout.gaps

class AddPlateDialog(resourced: Resourced) : Dialog<Plate>(), Resourced by resourced {

    private lateinit var plateChoice: ChoiceBox<PlatePrice>
    private lateinit var titleField: TextField
    private lateinit var qtyField: IntField
    private lateinit var priceField: DoubleField

    init {
        headerTitle = getString(R.string.add_plate)
        graphicIcon = ImageView(R.image.ic_plate)
        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.plate)) col 0 row 0
            plateChoice = choiceBox(transaction { PlatePrices.find().toObservableList() }!!) {
                valueProperty().listener { _, _, plate ->
                    priceField.value = plate.price
                }
            } col 1 row 0
            label(getString(R.string.title)) col 0 row 1
            titleField = textField { promptText = getString(R.string.title) } col 1 row 1
            label(getString(R.string.qty)) col 0 row 2
            qtyField = intField { promptText = getString(R.string.qty) } col 1 row 2
            label(getString(R.string.price)) col 0 row 3
            priceField = doubleField { promptText = getString(R.string.price) } col 1 row 3
        }
        cancelButton()
        okButton {
            disableProperty().bind(plateChoice.valueProperty().isNull or
                titleField.textProperty().isEmpty or
                qtyField.valueProperty.lessEq(0) or
                priceField.valueProperty.lessEq(0))
        }
        setResultConverter {
            if (it == CANCEL) null else Plate.new(
                plateChoice.value.name,
                titleField.text,
                qtyField.value,
                priceField.value)
        }
    }
}