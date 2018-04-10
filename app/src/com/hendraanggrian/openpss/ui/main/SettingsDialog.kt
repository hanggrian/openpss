package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_CURRENCY_COUNTRY
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_CURRENCY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.schemas.findGlobalSettings
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.onActionFilter
import com.hendraanggrian.openpss.utils.stringCell
import javafx.geometry.Pos.CENTER
import javafx.scene.control.CheckBox
import javafx.scene.control.Dialog
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.binding.and
import ktfx.beans.property.toProperty
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onEditCommit
import ktfx.layouts.checkBox
import ktfx.layouts.columns
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tableView
import ktfx.layouts.textField
import ktfx.layouts.vbox
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.control.textFieldCellFactory
import ktfx.scene.layout.gap

class SettingsDialog(resourced: Resourced, showGlobalSettings: Boolean) : Dialog<Unit>(), Resourced by resourced {

    private var isLocalChanged = false.toProperty()
    private lateinit var invoiceCheck: CheckBox

    private var isGlobalChanged = false.toProperty()
    private lateinit var languageField: TextField
    private lateinit var countryField: TextField

    init {
        headerTitle = getString(R.string.settings)
        graphicIcon = ImageView(R.image.ic_settings)
        dialogPane.content = vbox {
            spacing = 8.0
            label(getString(R.string.local_settings)) { font = getFont(R.font.opensans_bold, 16) }
            label(getString(R.string.invoice)) { font = getFont(R.font.opensans_bold) }
            invoiceCheck = checkBox(getString(R.string.quick_select_customer_when_adding_invoice)) {
                isSelected = SettingsFile.INVOICE_QUICK_SELECT_CUSTOMER
                selectedProperty().listener { isLocalChanged.set(true) }
            }
        }
        if (showGlobalSettings) dialogPane.expandableContent = vbox {
            tableView(transaction { GlobalSettings.find().toObservableList() }!!) {
                prefWidth = 480.0
                isEditable = true
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                columns {
                    getString(R.string.key)<String> { stringCell { transaction { key }!! } }
                    getString(R.string.value)<String> {
                        stringCell { transaction { value }!! }
                        textFieldCellFactory()
                        onEditCommit {
                            transaction {
                                GlobalSettings.find { key.equal(it.rowValue.key) }.projection { value }
                                    .update(it.newValue)
                                it.rowValue.value = it.newValue
                            }
                        }
                    }
                }
            }
        }
        if (showGlobalSettings) dialogPane.expandableContent = vbox {
            spacing = 8.0
            label(getString(R.string.global_settings)) { font = getFont(R.font.opensans_bold, 16) }
            gridPane {
                gap = 8.0
                transaction {
                    label("Currency") row 0 col 0
                    languageField = textField(findGlobalSettings(KEY_CURRENCY_LANGUAGE).single().value) {
                        promptText = "xx"
                        maxWidth = 48.0
                        alignment = CENTER
                        textProperty().listener { isGlobalChanged.set(true) }
                    } row 0 col 1
                    countryField = textField(findGlobalSettings(KEY_CURRENCY_COUNTRY).single().value) {
                        promptText = "XX"
                        maxWidth = 48.0
                        alignment = CENTER
                        textProperty().listener { isGlobalChanged.set(true) }
                    } row 0 col 2
                    label("Invoice header") row 1 col 0
                    textField() row 1 col 1 colSpans 2
                    textField() row 2 col 1 colSpans 2
                    textField() row 3 col 1 colSpans 2
                    textField() row 4 col 1 colSpans 2
                }
            }
        }
        cancelButton()
        okButton {
            disableProperty().bind(!isLocalChanged and !isGlobalChanged)
            onActionFilter {
                if (isLocalChanged.value) SettingsFile.run {
                    INVOICE_QUICK_SELECT_CUSTOMER = invoiceCheck.isSelected
                    save()
                }
                if (isGlobalChanged.value) transaction {
                    findGlobalSettings(KEY_CURRENCY_LANGUAGE).projection { value }.update(languageField.text)
                    findGlobalSettings(KEY_CURRENCY_COUNTRY).projection { value }.update(countryField.text)
                }
                close()
            }
        }
    }
}