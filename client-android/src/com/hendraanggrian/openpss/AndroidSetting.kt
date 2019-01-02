package com.hendraanggrian.openpss

import android.content.SharedPreferences

class AndroidSetting(val preferences: SharedPreferences) : Setting<SharedPreferences.Editor> {

    override fun getString(key: String): String? = preferences.getString(key, null)

    override fun getEditor(): SharedPreferences.Editor = preferences.edit()

    override fun SharedPreferences.Editor.save() = apply()
}