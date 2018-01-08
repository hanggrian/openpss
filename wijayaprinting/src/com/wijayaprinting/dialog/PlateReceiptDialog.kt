package com.wijayaprinting.dialog

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.PlateReceipt
import com.wijayaprinting.R
import com.wijayaprinting.Resourced
import com.wijayaprinting.utils.gap
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotfx.*

class PlateReceiptDialog(val resourced: Resourced) : Dialog<PlateReceipt>(), Resourced by resourced {

    private var customerProperty: ObjectProperty<Customer> = SimpleObjectProperty()

    init {
        title = getString(R.string.add_plate_receipt)
        headerText = getString(R.string.add_plate_receipt)
        graphic = ImageView(R.png.ic_document)
        content = gridPane {
            gap(8)
            label(getString(R.string.customer)) col 0 row 0
            button {
                textProperty() bind stringBindingOf(customerProperty) { customerProperty.value?.toString() ?: getString(R.string.search_customer) }
                setOnAction { SearchCustomerDialog(resourced).showAndWait().ifPresent { customerProperty.set(it) } }
            } col 1 row 0
        }
        buttons(CANCEL, OK)
        setResultConverter { null }
    }
}