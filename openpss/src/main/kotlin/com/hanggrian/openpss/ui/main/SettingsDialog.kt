@file:Suppress("ktlint:rulebook:qualifier-consistency")

package com.hanggrian.openpss.ui.main

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.Language
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.Space
import com.hanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_LANGUAGE
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.io.properties.PreferencesFile
import com.hanggrian.openpss.io.properties.PreferencesFile.WAGE_READER
import com.hanggrian.openpss.popup.dialog.Dialog
import com.hanggrian.openpss.ui.wage.readers.Reader
import com.jfoenix.controls.JFXButton
import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.nosql.update
import ktfx.bindings.and
import ktfx.booleanPropertyOf
import ktfx.collections.toObservableList
import ktfx.controls.LEFT
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.KtfxHBox
import ktfx.layouts.KtfxLayoutDslMarker
import ktfx.layouts.KtfxVBox
import ktfx.layouts.NodeContainer
import ktfx.layouts.borderPane
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.styledLabel
import ktfx.layouts.textArea
import ktfx.layouts.vbox
import ktfx.text.buildStringConverter
import kotlin.coroutines.CoroutineContext

class SettingsDialog(context: Context) : Dialog(context, R.string_settings) {
    private var isLocalChanged = booleanPropertyOf()
    private var isGlobalChanged = booleanPropertyOf()

    private lateinit var invoiceHeadersArea: TextArea
    private lateinit var wageReaderChoice: ComboBox<Reader>
    private lateinit var languageBox: ComboBox<Language>

    init {
        borderPane {
            left =
                ktfx.layouts.vbox {
                    spacing = getDouble(R.dimen_padding_large)
                    group(R.string_wage) {
                        item {
                            label(getString(R.string_reader))
                            wageReaderChoice =
                                jfxComboBox(Reader.listAll()) {
                                    value = Reader.of(WAGE_READER)
                                    valueProperty().listener { _, _, value ->
                                        isLocalChanged.set(true)
                                        WAGE_READER = (value as Reader).name
                                    }
                                }
                        }
                    }
                }
            addChild(Space(getDouble(R.dimen_padding_large)))
            right =
                this@SettingsDialog.group(R.string_global_settings) {
                    isDisable = !isAdmin()
                    gridPane {
                        hgap = getDouble(R.dimen_padding_medium)
                        vgap = getDouble(R.dimen_padding_medium)
                        transaction {
                            label(getString(R.string_server_language))
                                .grid(0, 0)
                            languageBox =
                                jfxComboBox(Language.entries.toObservableList()) {
                                    converter =
                                        buildStringConverter { toString { it!!.toString(true) } }
                                    selectionModel.select(
                                        Language.ofFullCode(
                                            findGlobalSettings(KEY_LANGUAGE).single().value,
                                        ),
                                    )
                                    valueProperty().listener { isGlobalChanged.set(true) }
                                }.grid(0, 1)
                            label(getString(R.string_invoice_headers))
                                .grid(1, 0)
                            invoiceHeadersArea =
                                textArea(
                                    findGlobalSettings(KEY_INVOICE_HEADERS)
                                        .single()
                                        .valueList
                                        .joinToString("\n")
                                        .trim(),
                                ) {
                                    promptText = getString(R.string_invoice_headers)
                                    setMaxSize(256.0, 88.0)
                                    textProperty().listener { _, oldValue, value ->
                                        when (INVOICE_HEADERS_DIVIDER) {
                                            in value -> text = oldValue
                                            else -> isGlobalChanged.set(true)
                                        }
                                    }
                                }.grid(1, 1)
                        }
                    }
                }
        }
        buttonManager.run {
            styledJfxButton(getString(R.string_ok), styleClass = arrayOf(R.style_raised)) {
                isDefaultButton = true
                buttonType = JFXButton.ButtonType.RAISED
                disableProperty().bind(!isLocalChanged and !isGlobalChanged)
                onActionFilter {
                    if (isLocalChanged.value) {
                        PreferencesFile.save()
                    }
                    if (isGlobalChanged.value) {
                        transaction {
                            findGlobalSettings(KEY_LANGUAGE)
                                .projection { value }
                                .update(languageBox.value.fullCode)
                            findGlobalSettings(KEY_INVOICE_HEADERS)
                                .projection { value }
                                .update(invoiceHeadersArea.text.trim().replace("\n", "|"))
                        }
                    }
                    close()
                }
            }
        }
    }

    private fun group(titleId: String, configuration: (@KtfxLayoutDslMarker KtfxVBox).() -> Unit) =
        ktfx.layouts.vbox(getDouble(R.dimen_padding_small)) {
            styledLabel(getString(titleId), null, R.style_bold)
            configuration()
        }

    private fun NodeContainer.group(
        titleId: String,
        configuration: (@KtfxLayoutDslMarker KtfxVBox).() -> Unit,
    ) = vbox(getDouble(R.dimen_padding_small)) {
        styledLabel(getString(titleId), null, R.style_bold)
        configuration()
    }

    private fun NodeContainer.item(
        labelId: String? = null,
        configuration: (@KtfxLayoutDslMarker KtfxHBox).() -> Unit,
    ) = hbox(getDouble(R.dimen_padding_medium)) {
        alignment = LEFT
        if (labelId != null) {
            label(getString(labelId))
        }
        configuration()
    }

    private companion object {
        const val INVOICE_HEADERS_DIVIDER = "|"

        /**
         * Can't use `javafxx-coroutines` because by the time `consume`
         * is called in coroutine context, it is already too late.
         */
        fun Node.onActionFilter(
            context: CoroutineContext = Dispatchers.JavaFx,
            action: suspend CoroutineScope.() -> Unit,
        ) = addEventFilter(ActionEvent.ACTION) {
            it.consume()
            GlobalScope.launch(context) { action() }
        }
    }
}
