package com.hendraanggrian.openpss

import android.content.SharedPreferences

class AndroidSetting(val preferences: SharedPreferences) : Setting<SharedPreferences.Editor> {

    override fun contains(key: String): Boolean = preferences.contains(key)

    override fun getString(key: String): String = preferences.getString(key, null)!!

    override fun getInt(key: String): Int = preferences.getInt(key, 0)

    override fun getEditor(): SharedPreferences.Editor = preferences.edit()

    override fun SharedPreferences.Editor.set(key: String, value: String) {
        putString(key, value)
    }

    override fun SharedPreferences.Editor.save() = apply()
}