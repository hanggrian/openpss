package com.hendraanggrian.openpss

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.defaults.SharedPreferencesDefaults
import com.hendraanggrian.defaults.toDefaults
import com.hendraanggrian.openpss.api.OpenPSSApi
import java.util.ResourceBundle

@Suppress("unused")
class OpenPSSApplication : Application(), StringResources {

    lateinit var api: OpenPSSApi

    private lateinit var _defaults: SharedPreferencesDefaults

    override lateinit var resourceBundle: ResourceBundle

    override fun onCreate() {
        super.onCreate()
        Bundler.setDebug(BuildConfig2.DEBUG)
        _defaults = PreferenceManager.getDefaultSharedPreferences(this).toDefaults().also {
            it.setDefault()
        }
        resourceBundle = _defaults.language.toResourcesBundle()
    }

    val defaults: SharedPreferencesDefaults get() = _defaults

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
