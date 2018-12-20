package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.Customer
import javafx.beans.binding.When
import javafx.scene.control.Label
import javafx.scene.control.ListView
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.eq
import ktfx.layouts.label
import ktfx.listeners.cellFactory

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
            When(selectionModel.selectedIndexProperty() eq index)
                then ktfx.layouts.imageView(selectedImageId)
                otherwise ktfx.layouts.imageView(unselectedImageId)
        )
}