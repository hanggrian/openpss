package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Event
import com.hendraanggrian.openpss.db.schemas.Events
import com.hendraanggrian.openpss.db.transaction

open class Action<R>(
    private val context: Context,
    val message: String,
    val action: SessionWrapper.() -> R
) {

    operator fun invoke(): R = transaction {
        val result = action()
        Events += Event.new(context.login.id, message)
        result
    }

    inline operator fun invoke(block: (R) -> Unit) = this().let(block)
}