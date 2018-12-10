package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.dialog.PermissionDialog
import javafx.scene.layout.StackPane
import javafx.util.StringConverter
import javafx.util.converter.CurrencyStringConverter
import javafx.util.converter.NumberStringConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.jfoenix.jfxSnackbar
import java.awt.Desktop
import kotlin.coroutines.CoroutineContext

/** StackPane is the root layout for [ktfx.jfoenix.jfxSnackbar]. */
interface FxComponent : Resources, Component<StackPane> {

    /** Number decimal string converter. */
    val numberConverter: StringConverter<Number>
        get() = NumberStringConverter()

    /** Number decimal with currency prefix string converter. */
    val currencyConverter: StringConverter<Number>
        get() = CurrencyStringConverter(transaction {
            Language.ofFullCode(findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value)
                .toLocale()
        })

    /** Returns [Desktop] instance, may be null if it is unsupported. */
    val desktop: Desktop?
        get() {
            if (!Desktop.isDesktopSupported()) {
                rootLayout.jfxSnackbar(
                    "java.awt.Desktop is not supported.",
                    App.DURATION_SHORT
                )
                return null
            }
            return Desktop.getDesktop()
        }

    suspend fun withPermission(
        context: CoroutineContext = Dispatchers.JavaFx,
        action: suspend () -> Unit
    ) {
        when {
            App.API.isAdmin(login.name) -> action()
            else -> PermissionDialog(this).show { admin ->
                when (admin) {
                    null -> rootLayout.jfxSnackbar(getString(R.string.invalid_password), App.DURATION_SHORT)
                    else -> GlobalScope.launch(context) { action() }
                }
            }
        }
    }
}