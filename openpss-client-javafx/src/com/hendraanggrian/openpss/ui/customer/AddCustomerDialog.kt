package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.ui.ResultableDialog
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.isPersonName
import javafx.beans.binding.When
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.eq
import ktfx.bindings.isBlank
import ktfx.bindings.or
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxTabPane
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.label
import ktfx.layouts.tab

class AddCustomerDialog(component: FxComponent) :
    ResultableDialog<Customer>(component, R2.string.add_customer) {

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
            bindText(R2.string.person, R2.string.company)
        }
        label {
            bindText(R2.string._person_requirement, R2.string._company_requirement)
            isWrapText = true
        }
        editor = jfxTextField {
            promptText = getString(R2.string.name)
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
                then ImageView(selectedImageId)
                otherwise ImageView(unselectedImageId)
        )
    }

    private fun Label.bindText(personTextId: String, companyTextId: String) = textProperty().bind(
        When(tabPane.selectionModel.selectedIndexProperty() eq 0)
            then getString(personTextId)
            otherwise getString(companyTextId)
    )

    override val nullableResult: Customer
        get() = Customer.new(
            editor.text.clean(),
            tabPane.selectionModel.selectedIndex == 1,
            runBlocking(Dispatchers.IO) { OpenPSSApi.getDate() })
}