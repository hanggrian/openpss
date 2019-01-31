package com.hendraanggrian.openpss

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.get
import com.hendraanggrian.openpss.api.OpenPssApi
import java.util.ResourceBundle

@Suppress("unused")
class OpenPssApplication : Application(), StringResources {

    lateinit var api: OpenPssApi

    private lateinit var _defaults: Defaults<*>

    override lateinit var resourceBundle: ResourceBundle

    override fun onCreate() {
        super.onCreate()
        Bundler.setDebug(BuildConfig2.DEBUG)
        _defaults = Defaults[PreferenceManager.getDefaultSharedPreferences(this)]
        _defaults.setDefault()
        resourceBundle = _defaults.language.toResourcesBundle()
    }

    val defaults: Defaults<*> get() = _defaults

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}