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
                stack.jfxSnackbar(
                    "java.awt.Desktop is not supported.",
                    App.DURATION_SHORT
                )
                return null
            }
            return Desktop.getDesktop()
        }
}