package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.schema.Customer
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import ktfx.cells.cellFactory
import ktfx.eq
import ktfx.given
import ktfx.layouts.label
import ktfx.otherwise
import ktfx.then

class CustomerListView : ListView<Customer>() {

    init {
        cellFactory {
            onUpdate { customer, empty ->
                if (customer != null && !empty) graphic = ktfx.layouts.vbox {
                    label(customer.name) {
                        when {
                            customer.isCompany -> bindGraphic(
                                index,
                                R.image.graphic_company_selected,
                                R.image.graphic_company
                            )
                            else -> bindGraphic(
                                index,
                                R.image.graphic_person_selected,
                                R.image.graphic_person
                            )
                        }
                    }
                }
            }
        }
    }

    private fun Label.bindGraphic(index: Int, selectedImageId: String, unselectedImageId: String) =
        graphicProperty().bind(
            given(selectionModel.selectedIndexProperty() eq index)
                then ImageView(selectedImageId)
                otherwise ImageView(unselectedImageId)
        )
}
