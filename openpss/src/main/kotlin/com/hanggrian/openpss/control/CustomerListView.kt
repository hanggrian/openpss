package com.hanggrian.openpss.control

import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Customer
import javafx.beans.binding.When
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.image.ImageView
import ktfx.bindings.eq
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.cells.cellFactory
import ktfx.layouts.label

class CustomerListView : ListView<Customer>() {
    init {
        cellFactory {
            onUpdate { customer, empty ->
                if (customer == null || empty) {
                    return@onUpdate
                }
                graphic =
                    ktfx.layouts.vbox {
                        label(customer.name) {
                            when {
                                customer.isCompany ->
                                    bindGraphic(
                                        index,
                                        R.image_graphic_company_selected,
                                        R.image_graphic_company,
                                    )
                                else ->
                                    bindGraphic(
                                        index,
                                        R.image_graphic_person_selected,
                                        R.image_graphic_person,
                                    )
                            }
                        }
                    }
            }
        }
    }

    private fun Label.bindGraphic(index: Int, selectedImageId: String, unselectedImageId: String) =
        graphicProperty()
            .bind(
                When(selectionModel.selectedIndexProperty() eq index)
                    then ImageView(selectedImageId)
                    otherwise ImageView(unselectedImageId),
            )
}
