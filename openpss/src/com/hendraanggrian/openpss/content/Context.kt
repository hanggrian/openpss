package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.scene.layout.StackPane
import ktfx.beans.property.toProperty
import ktfx.jfoenix.jfxSnackbar
import java.awt.Desktop

/** Usually being passed around as first constructor of many components. */
interface Context : Resources {

    val login: Employee

    val root: StackPane

    fun isAdmin(): Boolean = transaction { Employees[login].single().admin }

    fun isAdminProperty(): ReadOnlyBooleanProperty = isAdmin().toProperty()

    /** Returns [Desktop] instance, may be null if it is unsupported. */
    val desktop: Desktop?
        get() {
            if (!Desktop.isDesktopSupported()) {
                root.jfxSnackbar("java.awt.Desktop is not supported.",
                    App.DURATION_SHORT
                )
                return null
            }
            return Desktop.getDesktop()
        }
}