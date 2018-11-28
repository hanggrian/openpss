package com.hendraanggrian.openpss

import android.app.Application
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.mongodb.ServerAddress

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getString("server_port", null).isNullOrBlank()) {
            preferences.edit {
                putString("server_port", ServerAddress.defaultPort().toString())
            }
        }
    }
}