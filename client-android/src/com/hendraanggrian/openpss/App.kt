package com.hendraanggrian.openpss

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.hendraanggrian.bundler.Bundler
import com.mongodb.ServerAddress

@Suppress("unused")
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Bundler.setDebug(true)
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getString("server_port", null).isNullOrBlank()) {
            preferences.edit {
                putString("server_port", ServerAddress.defaultPort().toString())
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}