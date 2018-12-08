package com.hendraanggrian.openpss.content

import android.content.Context
import android.view.View

/** View is the root layout for snackbar and errorbar. */
interface AndroidComponent : Component<View> {

    val context: Context
}