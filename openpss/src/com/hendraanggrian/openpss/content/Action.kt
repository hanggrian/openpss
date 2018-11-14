package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.transaction
import ktfx.jfoenix.jfxSnackbar
import java.util.ResourceBundle

abstract class Action<T>(
    private val context: Context,
    private val requireAdmin: Boolean = false
) : Context by context {

    @Suppress("LeakingThis")
    override val resourceBundle: ResourceBundle = Language.ofServer().toResourcesBundle()

    abstract val log: String

    abstract fun SessionWrapper.handle(): T

    operator fun invoke(block: SessionWrapper.(T) -> Unit) {
        transaction {
            if (requireAdmin && !Employees[login].single().isAdmin) {
                root.jfxSnackbar(getString(R.string.admin_status_required), App.DURATION_SHORT)
            } else {
                block(handle())
                Logs += Log.new(context.login.id, log)
            }
        }
    }
}