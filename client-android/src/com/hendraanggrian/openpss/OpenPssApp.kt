package com.hendraanggrian.openpss

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.openpss.api.OpenPssApi
import java.util.ResourceBundle

@Suppress("unused")
class OpenPssApp : Application(), StringResources {

    lateinit var api: OpenPssApi

    private lateinit var _setting: AndroidSetting

    override lateinit var resourceBundle: ResourceBundle

    override fun onCreate() {
        super.onCreate()
        Bundler.setDebug(BuildConfig2.DEBUG)
        _setting = AndroidSetting(PreferenceManager.getDefaultSharedPreferences(this)).apply {
            editDefault()
            resourceBundle = language.toResourcesBundle()
        }
    }

    val setting: AndroidSetting get() = _setting

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}