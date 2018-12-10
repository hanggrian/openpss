package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.dialog.PermissionDialog
import kotlinx.nosql.Id
import ktfx.jfoenix.jfxSnackbar
import java.util.ResourceBundle

abstract class Action<T>(
    private val component: FxComponent,
    private val requireAdmin: Boolean = false
) : FxComponent by component {

    @Suppress("LeakingThis")
    override val resourceBundle: ResourceBundle = Language.ofServer().toResourcesBundle()

    abstract val log: String

    abstract fun SessionWrapper.handle(): T

    operator fun invoke(block: SessionWrapper.(T) -> Unit) {
        transaction {
            when {
                requireAdmin && !Employees[login].single().isAdmin -> {
                    PermissionDialog(component).show { admin ->
                        when (admin) {
                            null -> rootLayout.jfxSnackbar(getString(R.string.invalid_password), App.DURATION_SHORT)
                            else -> transaction { execute(block, admin.id) }
                        }
                    }
                }
                else -> execute(block)
            }
        }
    }

    private inline fun SessionWrapper.execute(
        block: (SessionWrapper.(T) -> Unit),
        adminId: Id<String, Employees>? = null
    ) {
        block(handle())
        Logs += Log.new(log, component.login.id, adminId)
    }
}