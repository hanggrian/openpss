package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.GlobalSettings.KEY_CURRENCY_COUNTRY
import com.hendraanggrian.openpss.db.schemas.GlobalSettings.KEY_CURRENCY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.GlobalSettings.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.findGlobalSettings
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile
import com.hendraanggrian.openpss.io.properties.SettingsFile.CUSTOMER_PAGINATION_ITEMS
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.getColor
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.onActionFilter
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotlinx.nosql.update
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.toProperty
import ktfx.beans.value.and
import ktfx.coroutines.listener
import ktfx.layouts.checkBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.slider
import ktfx.layouts.tabPane
import ktfx.layouts.textArea
import ktfx.layouts.textField
import ktfx.layouts.vbox
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import java.util.Currency
import java.util.Locale

class SettingsDialog(resourced: Resourced, showGlobalSettings: Boolean) : Dialog<Unit>(), Resourced by resourced {

    private companion object {
        const val CURRENCY_INVALID = "-"
        const val INVOICE_HEADERS_DIVIDER = "|"
    }

    private var isLocalChanged = false.toProperty()
    private var isGlobalChanged = false.toProperty()
    private lateinit var languageField: TextField
    private lateinit var countryField: TextField
    private lateinit var invoiceHeadersArea: TextArea

    init {
        headerTitle = getString(R.string.settings)
        graphicIcon = ImageView(R.image.ic_settings)
        dialogPane.content = tabPane {
            (getString(R.string.customer)) {
                slider(1.0, 50.0, CUSTOMER_PAGINATION_ITEMS.toDouble()) {

                }
            }
            (getString(R.string.invoice)) {
                checkBox(getString(R.string.quick_select_customer_when_adding_invoice)) {
                    isSelected = SettingsFile.INVOICE_QUICK_SELECT_CUSTOMER
                    selectedProperty().listener { _, _, value ->
                        isLocalChanged.set(true)
                        SettingsFile.INVOICE_QUICK_SELECT_CUSTOMER = value
                    }
                }
            }
        }
        dialogPane.content = vbox {
            spacing = 8.0
            label(getString(R.string.local_settings)) { font = getFont(R.font.opensans_bold, 16) }
            label(getString(R.string.customer)) { font = getFont(R.font.opensans_bold) }

            label(getString(R.string.invoice)) { font = getFont(R.font.opensans_bold) }

        }
        if (showGlobalSettings) dialogPane.expandableContent = vbox {
            spacing = 8.0
            label(getString(R.string.global_settings)) { font = getFont(R.font.opensans_bold, 16) }
            gridPane {
                gap = 8.0
                transaction {
                    label(getString(R.string.currency)) row 0 col 0
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
                    label {
                        font = getFont(R.font.opensans_bold)
                        textProperty().bind(stringBindingOf(languageField.textProperty(), countryField.textProperty()) {
                            try {
                                Currency.getInstance(Locale(languageField.text, countryField.text)).symbol
                            } catch (e: Exception) {
                                CURRENCY_INVALID
                            }
                        })
                        textFillProperty().bind(bindingOf(textProperty()) {
                            getColor(if (text == CURRENCY_INVALID) R.color.red else R.color.teal)
                        })
                    } row 0 col 3
                    label(getString(R.string.invoice_headers)) row 1 col 0
                    invoiceHeadersArea = textArea(findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
                        .joinToString("\n").trim()) {
                        setMaxSize(256.0, 88.0)
                        textProperty().listener { _, oldValue, value ->
                            when (INVOICE_HEADERS_DIVIDER) {
                                in value -> text = oldValue
                                else -> isGlobalChanged.set(true)
                            }
                        }
                    } row 1 col 1 colSpans 3
                }
            }
        }
        cancelButton()
        okButton {
            disableProperty().bind(!isLocalChanged and !isGlobalChanged)
            onActionFilter {
                if (isLocalChanged.value) SettingsFile.save()
                if (isGlobalChanged.value) transaction {
                    findGlobalSettings(KEY_CURRENCY_LANGUAGE).projection { value }.update(languageField.text)
                    findGlobalSettings(KEY_CURRENCY_COUNTRY).projection { value }.update(countryField.text)
                    findGlobalSettings(KEY_INVOICE_HEADERS).projection { value }
                        .update(invoiceHeadersArea.text.trim().replace("\n", "|"))
                }
                close()
            }
        }
    }
}