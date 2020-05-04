package com.hendraanggrian.openpss

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.prefy.Prefy
import com.hendraanggrian.prefy.android.AndroidPreferences
import com.hendraanggrian.prefy.android.get
import java.util.ResourceBundle

class OpenPssApp : Application(), StringResources {

    lateinit var api: OpenPSSApi

    private lateinit var _prefs: AndroidPreferences

    override lateinit var resourceBundle: ResourceBundle

    override fun onCreate() {
        super.onCreate()
        Bundler.setDebug(BuildConfig2.DEBUG)
        _prefs = Prefy[this].also { it.setDefault() }
        resourceBundle = _prefs.language.toResourcesBundle()
    }

    val defaults: AndroidPreferences get() = _prefs

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
