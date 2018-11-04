package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.transaction
import java.util.ResourceBundle

abstract class Action<R>(private val context: Context) : Context by context {

    @Suppress("LeakingThis")
    override val resources: ResourceBundle = Language.server().toResourcesBundle()

    abstract val message: String

    abstract fun SessionWrapper.handle(): R

    operator fun invoke(): R = transaction {
        val result = handle()
        Logs += Log.new(context.login.id, message)
        result
    }

    inline operator fun invoke(block: (R) -> Unit) = invoke().let(block)
}