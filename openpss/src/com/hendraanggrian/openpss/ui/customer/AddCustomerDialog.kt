package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isPersonName
import javafx.beans.binding.When
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.eq
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxTabPane
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.label
import ktfx.layouts.tab

class AddCustomerDialog(context: Context) : ResultableDialog<Customer>(context, R.string.add_customer) {

    private companion object {
        const val WIDTH = 300.0
    }

    private val tabPane: TabPane
    private val editor: TextField

    override val focusedNode: Node? get() = editor

    init {
        contentPane.run {
            minWidth = WIDTH
            maxWidth = WIDTH
        }
        tabPane = jfxTabPane {
            tab {
                bindGraphic(R.image.display_person_selected, R.image.display_person)
            }
            tab {
                bindGraphic(R.image.display_company_selected, R.image.display_company)
            }
        }
        label {
            styleClass += R.style.bold
            bindText(R.string.person, R.string.company)
        }
        label {
            bindText(R.string._person_requirement, R.string._company_requirement)
            isWrapText = true
        }
        editor = jfxTextField {
            promptText = getString(R.string.name)
        }
        defaultButton.disableProperty().bind(
            When(tabPane.selectionModel.selectedIndexProperty() eq 0)
                then (editor.textProperty().isBlank() or !editor.textProperty().isPersonName())
                otherwise editor.textProperty().isBlank()
        )
        tabPane.selectionModel.selectedIndexProperty().listener {
            editor.requestFocus()
        }
    }

    private fun Tab.bindGraphic(selectedImageId: String, unselectedImageId: String) {
        graphicProperty().bind(
            When(selectedProperty())
                then ktfx.layouts.imageView(selectedImageId)
                otherwise ktfx.layouts.imageView(unselectedImageId)
        )
    }

    private fun Label.bindText(personTextId: String, companyTextId: String) = textProperty().bind(
        When(tabPane.selectionModel.selectedIndexProperty() eq 0)
            then getString(personTextId)
            otherwise getString(companyTextId)
    )

    override val nullableResult: Customer?
        get() = Customer.new(
            editor.text.clean(),
            tabPane.selectionModel.selectedIndex == 1
        )
}