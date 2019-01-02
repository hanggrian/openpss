package com.hendraanggrian.openpss

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.preference.PreferenceManager

/** View is the root layout for snackbar and errorbar. */
interface AndroidComponent :
    Component<View, AndroidSetting, SharedPreferences.Editor>,
    StringResources {

    /** To be overriden with dialog, this has to be function instead of type. */
    fun getContext(): Context?

    override val setting: AndroidSetting
        get() = AndroidSetting(PreferenceManager.getDefaultSharedPreferences(getContext()!!))
}