package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_CURRENCY_COUNTRY
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_CURRENCY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile
import com.hendraanggrian.openpss.io.properties.SettingsFile.CUSTOMER_PAGINATION_ITEMS
import com.hendraanggrian.openpss.io.properties.SettingsFile.INVOICE_PAGINATION_ITEMS
import com.hendraanggrian.openpss.io.properties.SettingsFile.INVOICE_QUICK_SELECT_CUSTOMER
import com.hendraanggrian.openpss.io.properties.SettingsFile.WAGE_READER
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.util.clearConverters
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.onActionFilter
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import kotlinx.nosql.update
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.toProperty
import ktfx.beans.value.and
import ktfx.collections.observableListOf
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._HBox
import ktfx.layouts._VBox
import ktfx.layouts.checkBox
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.textField
import ktfx.layouts.vbox
import ktfx.listeners.converter
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import java.util.Currency
import java.util.Locale

class SettingsDialog(resourced: Resourced, showGlobalSettings: Boolean) : Dialog<Nothing>(), Resourced by resourced {

    private companion object {
        const val CURRENCY_INVALID = "-"
        const val INVOICE_HEADERS_DIVIDER = "|"
    }

    private var isLocalChanged = false.toProperty()
    private var isGlobalChanged = false.toProperty()

    private lateinit var customerPaginationChoice: ChoiceBox<Int>
    private lateinit var invoicePaginationChoice: ChoiceBox<Int>
    private lateinit var invoiceHeadersArea: TextArea
    private lateinit var wageReaderChoice: ChoiceBox<Any>

    private lateinit var languageField: TextField
    private lateinit var countryField: TextField

    init {
        headerTitle = getString(R.string.settings)
        graphicIcon = ImageView(R.image.header_settings)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = vbox {
                spacing = 16.0
                group(R.string.customer) {
                    item(R.string.items_per_page) {
                        customerPaginationChoice = paginationChoice(CUSTOMER_PAGINATION_ITEMS) {
                            valueProperty().listener { _, _, value ->
                                isLocalChanged.set(true)
                                CUSTOMER_PAGINATION_ITEMS = value
                            }
                        }
                    }
                }
                group(R.string.invoice) {
                    item(R.string.items_per_page) {
                        invoicePaginationChoice = paginationChoice(INVOICE_PAGINATION_ITEMS) {
                            valueProperty().listener { _, _, value ->
                                isLocalChanged.set(true)
                                INVOICE_PAGINATION_ITEMS = value
                            }
                        }
                    }
                    checkBox(getString(R.string.quick_select_customer_when_adding_invoice)) {
                        isSelected = SettingsFile.INVOICE_QUICK_SELECT_CUSTOMER
                        selectedProperty().listener { _, _, value ->
                            isLocalChanged.set(true)
                            INVOICE_QUICK_SELECT_CUSTOMER = value
                        }
                    }
                }
                group(R.string.wage) {
                    item {
                        label(getString(R.string.reader))
                        wageReaderChoice = choiceBox(Reader.listAll()) {
                            value = Reader.of(WAGE_READER)
                            valueProperty().listener { _, _, value ->
                                isLocalChanged.set(true)
                                WAGE_READER = (value as Reader).name
                            }
                        }
                    }
                }
            }
            if (showGlobalSettings) expandableContent = group(R.string.global_settings) {
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
                            font = getFont(R.font.sf_pro_text_bold)
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
                    clearConverters()
                }
                close()
            }
        }
    }

    private fun group(titleId: String, init: (@LayoutDsl _VBox).() -> Unit): VBox = vbox {
        spacing = 4.0
        label(getString(titleId)) { font = getFont(R.font.sf_pro_text_bold) }
        init()
    }

    private fun LayoutManager<Node>.group(titleId: String, init: (@LayoutDsl _VBox).() -> Unit): VBox = vbox {
        spacing = 4.0
        label(getString(titleId)) { font = getFont(R.font.sf_pro_text_bold) }
        init()
    }

    private fun LayoutManager<Node>.item(labelId: String? = null, init: (@LayoutDsl _HBox).() -> Unit): HBox = hbox {
        alignment = CENTER_LEFT
        spacing = 8.0
        if (labelId != null) label(getString(labelId))
        init()
    }

    private fun LayoutManager<Node>.paginationChoice(
        prefill: Int,
        init: (@LayoutDsl ChoiceBox<Int>).() -> Unit
    ): ChoiceBox<Int> = choiceBox(observableListOf(20, 30, 40, 50)) {
        converter {
            fromString { it.toInt() }
            toString { "$it ${getString(R.string.items)}" }
        }
        value = prefill
        init()
    }
}