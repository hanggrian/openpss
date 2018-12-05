package com.hendraanggrian.openpss.content

import android.content.Context
import android.view.View
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction

/** View is the root layout for snackbar and errorbar. */
interface AndroidComponent : Component<View> {

    val context: Context

    override fun isAdmin(): Boolean = transaction { Employees[login].single().isAdmin }
}