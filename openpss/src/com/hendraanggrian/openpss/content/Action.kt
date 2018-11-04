package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.transaction
import java.util.ResourceBundle

abstract class Action<R>(private val context: Context) : Context by context {

    @Suppress("LeakingThis")
    override val resources: ResourceBundle = Language.server().toResourcesBundle()

    abstract val log: String

    abstract fun SessionWrapper.handle(): R

    operator fun invoke(block: SessionWrapper.(R) -> Unit) = transaction {
        val result = handle()
        Logs += Log.new(context.login.id, log)
        block(result)
    }
}