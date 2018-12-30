package com.hendraanggrian.openpss.ui

import android.content.Context
import android.view.View
import com.hendraanggrian.openpss.api.OpenPSSApi

/** View is the root layout for snackbar and errorbar. */
interface AndroidComponent : Component<View> {

    /** To be overriden with dialog, this has to be function instead of type. */
    fun getContext(): Context?

    val api: OpenPSSApi
}