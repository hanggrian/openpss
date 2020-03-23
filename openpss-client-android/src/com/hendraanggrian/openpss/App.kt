package com.hendraanggrian.openpss

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.android.AndroidPrefs
import com.hendraanggrian.prefs.android.get
import java.util.ResourceBundle

class App : Application(), StringResources {

    lateinit var api: OpenPSSApi

    private lateinit var _prefs: AndroidPrefs

    override lateinit var resourceBundle: ResourceBundle

    override fun onCreate() {
        super.onCreate()
        Bundler.setDebug(BuildConfig2.DEBUG)
        _prefs = Prefs[this].also { it.setDefault() }
        resourceBundle = _prefs.language.toResourcesBundle()
    }

    val defaults: AndroidPrefs get() = _prefs

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
