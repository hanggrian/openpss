package com.hendraanggrian.openpss

import android.app.Application
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.mongodb.ServerAddress
import org.apache.log4j.BasicConfigurator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            BasicConfigurator.configure()
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getString("server_port", null).isNullOrBlank()) {
            preferences.edit {
                putString("server_port", ServerAddress.defaultPort().toString())
            }
        }
    }
}