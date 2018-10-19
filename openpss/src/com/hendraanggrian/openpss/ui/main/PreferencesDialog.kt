package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.clearConverters
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.onActionFilter
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_LANGUAGE
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Language
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.io.properties.PreferencesFile.WAGE_READER
import com.hendraanggrian.openpss.popup.dialog.Dialog
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import kotlinx.nosql.update
import ktfx.NodeManager
import ktfx.annotations.LayoutDsl
import ktfx.beans.property.toMutableProperty
import ktfx.beans.value.and
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxButton
import ktfx.layouts._HBox
import ktfx.layouts._VBox
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.vbox
import ktfx.listeners.converter
import ktfx.scene.layout.gap

class PreferencesDialog(
    resourced: Resourced,
    private val showGlobalSettings: Boolean
) : Dialog(resourced, R.string.preferences) {

    private companion object {
        const val INVOICE_HEADERS_DIVIDER = "|"
    }

    private var isLocalChanged = false.toMutableProperty()
    private var isGlobalChanged = false.toMutableProperty()

    private lateinit var invoiceHeadersArea: TextArea
    private lateinit var wageReaderChoice: ChoiceBox<Any>

    private lateinit var languageBox: ChoiceBox<Language>

    init {
        vbox {
            spacing = R.dimen.padding_large.toDouble()
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
        if (showGlobalSettings) {
            group(R.string.global_settings) {
                gridPane {
                    gap = R.dimen.padding_medium.toDouble()
                    transaction {
                        label(getString(R.string.server_language)) row 0 col 0
                        languageBox = choiceBox(Language.values().toObservableList()) {
                            converter { toString { it!!.toString(true) } }
                            selectionModel.select(Language.ofFullCode(findGlobalSettings(KEY_LANGUAGE).single().value))
                            valueProperty().listener { isGlobalChanged.set(true) }
                        } row 0 col 1
                        label(getString(R.string.invoice_headers)) row 1 col 0
                        invoiceHeadersArea = textArea(
                            findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
                                .joinToString("\n").trim()
                        ) {
                            setMaxSize(256.0, 88.0)
                            textProperty().listener { _, oldValue, value ->
                                when (INVOICE_HEADERS_DIVIDER) {
                                    in value -> text = oldValue
                                    else -> isGlobalChanged.set(true)
                                }
                            }
                        } row 1 col 1
                    }
                }
            }
        }
        buttonManager.run {
            jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += App.STYLE_BUTTON_RAISED
                buttonType = JFXButton.ButtonType.RAISED
                disableProperty().bind(!isLocalChanged and !isGlobalChanged)
                onActionFilter {
                    if (isLocalChanged.value) PreferencesFile.save()
                    if (isGlobalChanged.value) transaction {
                        findGlobalSettings(KEY_LANGUAGE).projection { value }.update(languageBox.value.fullCode)
                        findGlobalSettings(KEY_INVOICE_HEADERS).projection { value }
                            .update(invoiceHeadersArea.text.trim().replace("\n", "|"))
                        clearConverters()
                    }
                    close()
                }
            }
        }
    }

    private fun group(
        titleId: String,
        init: (@LayoutDsl _VBox).() -> Unit
    ): VBox = ktfx.layouts.vbox(R.dimen.padding_small.toDouble()) {
        label(getString(titleId)) { font = bold() }
        init()
    }

    private fun NodeManager.group(
        titleId: String,
        init: (@LayoutDsl _VBox).() -> Unit
    ): VBox = vbox(R.dimen.padding_small.toDouble()) {
        label(getString(titleId)) { font = bold() }
        init()
    }

    private fun NodeManager.item(
        labelId: String? = null,
        init: (@LayoutDsl _HBox).() -> Unit
    ): HBox = hbox(R.dimen.padding_medium.toDouble()) {
        alignment = CENTER_LEFT
        if (labelId != null) label(getString(labelId))
        init()
    }
}