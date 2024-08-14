package com.hanggrian.openpss.ui.customer

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.popup.dialog.ResultableDialog
import com.hanggrian.openpss.util.clean
import com.hanggrian.openpss.util.isPersonName
import javafx.beans.binding.When
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.eq
import ktfx.bindings.or
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxTabPane
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.label
import ktfx.layouts.styledLabel
import ktfx.layouts.tab

class AddCustomerDialog(context: Context) :
    ResultableDialog<Customer>(context, R.string_add_customer) {
    private val tabPane: TabPane
    private val editor: TextField

    init {
        contentPane.run {
            minWidth = WIDTH
            maxWidth = WIDTH
        }
        tabPane =
            jfxTabPane {
                tab { bindGraphic(R.image_display_person_selected, R.image_display_person) }
                tab { bindGraphic(R.image_display_company_selected, R.image_display_company) }
            }
        styledLabel(styleClass = arrayOf(R.style_bold)) {
            bindText(R.string_person, R.string_company)
        }
        label {
            bindText(R.string__person_requirement, R.string__company_requirement)
            isWrapText = true
        }
        editor = jfxTextField { promptText = getString(R.string_name) }
        defaultButton.disableProperty().bind(
            When(tabPane.selectionModel.selectedIndexProperty() eq 0) then
                (
                    editor.textProperty().booleanBindingBy { it.isNullOrBlank() } or
                        !editor.textProperty().isPersonName()
                ) otherwise editor.textProperty().booleanBindingBy { it.isNullOrBlank() },
        )
        tabPane.selectionModel.selectedIndexProperty().listener {
            editor.requestFocus()
        }
    }

    override val focusedNode: Node get() = editor

    private fun Tab.bindGraphic(selectedImageId: String, unselectedImageId: String) {
        graphicProperty().bind(
            When(selectedProperty())
                then ImageView(selectedImageId)
                otherwise ImageView(unselectedImageId),
        )
    }

    private fun Label.bindText(personTextId: String, companyTextId: String) =
        textProperty().bind(
            When(tabPane.selectionModel.selectedIndexProperty() eq 0)
                then getString(personTextId)
                otherwise getString(companyTextId),
        )

    override val nullableResult: Customer
        get() =
            Customer.new(
                editor.text.clean(),
                tabPane.selectionModel.selectedIndex == 1,
            )

    private companion object {
        const val WIDTH = 300.0
    }
}
