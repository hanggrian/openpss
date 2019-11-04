package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.FxSetting
import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.Space
import com.hendraanggrian.openpss.schema.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.schema.GlobalSetting.Companion.KEY_LANGUAGE
import com.hendraanggrian.openpss.ui.BaseDialog
import com.hendraanggrian.openpss.ui.wage.WageReader
import com.jfoenix.controls.JFXButton
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktfx.asProperty
import ktfx.bindings.and
import ktfx.buildStringConverter
import ktfx.collections.toObservableList
import ktfx.controls.gap
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxHBox
import ktfx.layouts.KtfxVBox
import ktfx.layouts.LayoutDslMarker
import ktfx.layouts.NodeManager
import ktfx.layouts.borderPane
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.vbox

class SettingsDialog(component: FxComponent) : BaseDialog(component, R2.string.settings) {

    private var isLocalChanged = false.asProperty()
    private var isGlobalChanged = false.asProperty()

    private lateinit var invoiceHeadersArea: TextArea
    private lateinit var wageReaderChoice: ComboBox<WageReader>
    private lateinit var languageBox: ComboBox<Language>

    init {
        borderPane {
            left = ktfx.layouts.vbox {
                spacing = getDouble(R.value.padding_large)
                group(R2.string.wage) {
                    item {
                        label(getString(R2.string.reader))
                        wageReaderChoice = jfxComboBox(WageReader.listAll()) {
                            value = WageReader.of(defaults[FxSetting.KEY_WAGEREADER]!!)
                            valueProperty().listener { isLocalChanged.set(true) }
                        }
                    }
                }
            }
            addNode(Space(getDouble(R.value.padding_large)))
            right = this@SettingsDialog.group(R2.string.global_settings) {
                isDisable = runBlocking(Dispatchers.IO) { !OpenPSSApi.isAdmin(login) }
                gridPane {
                    gap = getDouble(R.value.padding_medium)
                    label(getString(R2.string.server_language)) row 0 col 0
                    languageBox = jfxComboBox(Language.values().toObservableList()) {
                        converter = buildStringConverter { toString { it!!.toString(true) } }
                        selectionModel.select(
                            Language.ofFullCode(
                                runBlocking(Dispatchers.IO) { OpenPSSApi.getSetting(KEY_LANGUAGE).value }
                            )
                        )
                        valueProperty().listener { isGlobalChanged.set(true) }
                    } row 0 col 1
                    label(getString(R2.string.invoice_headers)) row 1 col 0
                    invoiceHeadersArea = textArea(
                        runBlocking(Dispatchers.IO) { OpenPSSApi.getSetting(KEY_INVOICE_HEADERS) }
                            .valueList
                            .joinToString("\n")
                            .trim()
                    ) {
                        promptText = getString(R2.string.invoice_headers)
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
        buttonManager.run {
            jfxButton(getString(R2.string.ok)) {
                isDefaultButton = true
                styleClass += R.style.raised
                buttonType = JFXButton.ButtonType.RAISED
                disableProperty().bind(!isLocalChanged and !isGlobalChanged)
                onActionFilter(Dispatchers.JavaFx) {
                    if (isLocalChanged.value) {
                        defaults {
                            set(FxSetting.KEY_WAGEREADER, wageReaderChoice.value.name)
                        }
                    }
                    if (isGlobalChanged.value) {
                        OpenPSSApi.setSetting(KEY_LANGUAGE, languageBox.value.fullCode)
                        OpenPSSApi.setSetting(
                            KEY_INVOICE_HEADERS,
                            invoiceHeadersArea.text.trim().replace("\n", "|")
                        )
                    }
                    close()
                }
            }
        }
    }

    private fun group(
        titleId: String,
        init: (@LayoutDslMarker KtfxVBox).() -> Unit
    ): VBox = ktfx.layouts.vbox(getDouble(R.value.padding_small)) {
        label(getString(titleId)) {
            styleClass += R.style.bold
        }
        init()
    }

    private fun NodeManager.group(
        titleId: String,
        init: (@LayoutDslMarker KtfxVBox).() -> Unit
    ): VBox = vbox(getDouble(R.value.padding_small)) {
        label(getString(titleId)) {
            styleClass += R.style.bold
        }
        init()
    }

    private fun NodeManager.item(
        labelId: String? = null,
        init: (@LayoutDslMarker KtfxHBox).() -> Unit
    ): HBox = hbox(getDouble(R.value.padding_medium)) {
        alignment = Pos.CENTER_LEFT
        if (labelId != null) label(getString(labelId))
        init()
    }

    private companion object {

        const val INVOICE_HEADERS_DIVIDER = "|"

        /**
         * Can't use `javafxx-coroutines` because by the time `consume`
         * is called in coroutine context, it is already too late.
         */
        fun Node.onActionFilter(
            context: CoroutineContext = Dispatchers.JavaFx,
            action: suspend CoroutineScope.() -> Unit
        ) = addEventFilter(ActionEvent.ACTION) {
            it.consume()
            GlobalScope.launch(context) { action() }
        }
    }
}
