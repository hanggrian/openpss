package com.hendraanggrian.openpss.db

import com.hendraanggrian.material.errorbar.errorbar
import com.hendraanggrian.openpss.content.AndroidComponent
import com.jakewharton.processphoenix.ProcessPhoenix

fun <T> AndroidComponent.transaction(statement: SessionWrapper.() -> T): T = try {
    Database.withSession(statement)
} catch (e: IllegalStateException) {
    rootLayout.errorbar(e.message.toString()){
        setAction(android.R.string.ok) {
            ProcessPhoenix.triggerRebirth(context)
        }
    }
    throw RuntimeException()
}