package com.hendraanggrian.openpss

import com.hendraanggrian.defaults.WritableDefaults
import com.hendraanggrian.openpss.api.GitHubApi
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.schema.GlobalSetting
import com.hendraanggrian.openpss.ui.ResultableDialog
import java.awt.Desktop
import java.lang.ref.WeakReference
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.PasswordField
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.StringConverter
import javafx.util.converter.CurrencyStringConverter
import javafx.util.converter.NumberStringConverter
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktfx.bindings.isBlank
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.controls.gap
import ktfx.jfoenix.jfxComboBox
import ktfx.jfoenix.jfxPasswordField
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.gridPane
import ktfx.layouts.label

/** StackPane is the root layout for [ktfx.jfoenix.jfxSnackbar]. */
interface FxComponent : Component<StackPane, WritableDefaults>, StringResources, ValueResources {

    companion object {
        private var apiRef = WeakReference<OpenPSSApi?>(null)
        private var gitHubApiRef = WeakReference<GitHubApi?>(null)
    }

    override val api: OpenPSSApi
        get() {
            var api = apiRef.get()
            if (api == null) {
                api = OpenPSSApi(
                    defaults[Setting.KEY_SERVER_HOST]!!,
                    defaults.getInt(Setting.KEY_SERVER_PORT)!!
                )
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
        get() = CurrencyStringConverter(runBlocking(Dispatchers.IO) {
            Language.ofFullCode(
                api.getSetting(GlobalSetting.KEY_LANGUAGE).value
            ).toLocale()
        })

    /** Returns [Desktop] instance, may be null if it is unsupported. */
    val desktop: Desktop?
        get() {
            if (!Desktop.isDesktopSupported()) {
                rootLayout.jfxSnackbar(
                    "java.awt.Desktop is not supported.",
                    getLong(R.value.duration_short)
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
                    null -> rootLayout.jfxSnackbar(
                        getString(R2.string.invalid_password),
                        getLong(R.value.duration_short)
                    )
                    else -> launch(context) { action() }
                }
            }
        }
    }

    fun getColor(id: String): Color = Color.web(valueProperties.getProperty(id))

    private class PermissionDialog(component: FxComponent) :
        ResultableDialog<Employee>(component, R2.string.permission_required) {

        private lateinit var adminCombo: ComboBox<Employee>
        private lateinit var passwordField: PasswordField

        override val focusedNode: Node? get() = adminCombo

        init {
            gridPane {
                gap = getDouble(R.value.padding_medium)
                label {
                    text = getString(R2.string._permission_required)
                } col 0 row 0 colSpans 2
                label(getString(R2.string.admin)) col 0 row 1
                adminCombo = jfxComboBox(runBlocking(Dispatchers.IO) { api.getEmployees() }
                    .filter { it.isAdmin && it.name != Employee.BACKDOOR.name }
                    .toObservableList()
                ) {
                    promptText = getString(R2.string.admin)
                } col 1 row 1
                label(getString(R2.string.password)) col 0 row 2
                passwordField = jfxPasswordField {
                    promptText = getString(R2.string.password)
                } col 1 row 2
            }
            defaultButton.disableProperty().bind(
                adminCombo.valueProperty().isNull or passwordField.textProperty().isBlank()
            )
        }

        override val nullableResult: Employee?
            get() = runBlocking(Dispatchers.IO) {
                api.login(
                    adminCombo.value.name,
                    passwordField.text
                )
            }
    }
}
