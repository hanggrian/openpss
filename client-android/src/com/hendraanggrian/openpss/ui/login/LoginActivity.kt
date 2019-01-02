package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.AndroidSetting
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.OpenPssActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : OpenPssActivity() {

    private lateinit var _setting: AndroidSetting
    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, _ ->
            loginButton.isEnabled =
                !preferences.getString("server_address", null).isNullOrBlank() &&
                !preferences.getString("employee", null).isNullOrBlank()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        replaceFragment(R.id.preferenceLayout, LoginFragment())
        _setting = setting
        preferenceListener.onSharedPreferenceChanged(_setting.preferences, null) // trigger once
    }

    override fun onResume() {
        super.onResume()
        _setting.preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        _setting.preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun login(@Suppress("UNUSED_PARAMETER") view: View) {
        openPssApplication.initApi(_setting.getString("server_address") ?: "localhost")
        PasswordDialogFragment()
            .args(extrasOf<PasswordDialogFragment>(_setting.getString("employee")!!))
            .show(supportFragmentManager)
    }
}