package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.db.transaction
import javafx.scene.layout.StackPane
import javafx.util.StringConverter
import javafx.util.converter.CurrencyStringConverter
import javafx.util.converter.NumberStringConverter
import ktfx.jfoenix.jfxSnackbar
import java.awt.Desktop

/** Usually being passed around as first constructor of many components. */
interface Context : Resources {

    val stack: StackPane

    val login: Employee

    fun isAdmin(): Boolean = transaction { Employees[login].single().isAdmin }

    /**
     * Some string converters are used quite often in some cases (controllers, dialogs, etc.).
     * To avoid creating the same instances over and over again, we cache those converters in this weak map for reuse,
     * using its class name as key.
     */
    val stringConverters: MutableMap<String, StringConverter<Number>>

    /** Number decimal string converter. */
    val numberConverter: StringConverter<Number>
        get() = stringConverters.getOrPut("number") { NumberStringConverter() }

    /** Number decimal with currency prefix string converter. */
    val currencyConverter: StringConverter<Number>
        get() = stringConverters.getOrPut("currency") {
            CurrencyStringConverter(transaction {
                Language.ofFullCode(findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value)
                    .toLocale()
            })
        }

    fun clearConverters() = stringConverters.clear()

    /** Returns [Desktop] instance, may be null if it is unsupported. */
    val desktop: Desktop?
        get() {
            if (!Desktop.isDesktopSupported()) {
                stack.jfxSnackbar(
                    "java.awt.Desktop is not supported.",
                    App.DURATION_SHORT
                )
                return null
            }
            return Desktop.getDesktop()
        }
}