package com.hendraanggrian.openpss.ui

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.preference.PreferenceManager

/** View is the root layout for snackbar and errorbar. */
interface AndroidComponent : Component<View> {

    /** To be overriden with dialog, this has to be function instead of type. */
    fun getContext(): Context?

    val defaultPreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(
            getContext()!!
        )
}