package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.api.GitHubApi
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.popup.dialog.PermissionDialog
import javafx.scene.layout.StackPane
import javafx.util.StringConverter
import javafx.util.converter.CurrencyStringConverter
import javafx.util.converter.NumberStringConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktfx.jfoenix.jfxSnackbar
import java.awt.Desktop
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/** StackPane is the root layout for [ktfx.jfoenix.jfxSnackbar]. */
interface FxComponent : Resources, Component<StackPane> {

    companion object {
        private var apiRef = WeakReference<OpenPSSApi?>(null)
        private var gitHubApiRef = WeakReference<GitHubApi?>(null)
    }

    val api: OpenPSSApi
        get() {
            var api = apiRef.get()
            if (api == null) {
                api = OpenPSSApi()
                apiRef = WeakReference(api)
            }
            return api
        }

    val gitHubApi: GitHubApi
        get() {
            var api = gitHubApiRef.get()
            if (api == null) {
                api = GitHubApi()
                gitHubApiRef = WeakReference(api)
            }
            return api
        }

    /** Number decimal string converter. */
    val numberConverter: StringConverter<Number>
        get() = NumberStringConverter()

    /** Number decimal with currency prefix string converter. */
    val currencyConverter: StringConverter<Number>
        get() = CurrencyStringConverter(runBlocking {
            Language.ofFullCode(api.getGlobalSetting(GlobalSetting.KEY_LANGUAGE).value).toLocale()
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

    suspend fun CoroutineScope.withPermission(
        context: CoroutineContext = Dispatchers.JavaFx,
        action: suspend CoroutineScope.() -> Unit
    ) {
        when {
            api.isAdmin(login) -> action()
            else -> PermissionDialog(this@FxComponent).show { admin ->
                when (admin) {
                    null -> rootLayout.jfxSnackbar(getString(R.string.invalid_password), App.DURATION_SHORT)
                    else -> GlobalScope.launch(context) { action() }
                }
            }
        }
    }
}