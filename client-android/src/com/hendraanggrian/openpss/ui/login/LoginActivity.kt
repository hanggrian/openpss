package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.OpenPssActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : OpenPssActivity() {

    private lateinit var preferences: SharedPreferences
    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, _ ->
            loginButton.isEnabled = !preferences.getString("server_address", null).isNullOrBlank()
                && !preferences.getString("employee", null).isNullOrBlank()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        replaceFragment(R.id.preferenceLayout, LoginFragment())
        preferences = defaultPreferences
        preferenceListener.onSharedPreferenceChanged(preferences, null) // trigger once
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun login(@Suppress("UNUSED_PARAMETER") view: View) {
        openPssApplication.initApi(preferences.getString("server_address", "localhost")!!)
        PasswordDialogFragment()
            .args(extrasOf<PasswordDialogFragment>(preferences.getString("employee", null)!!))
            .show(supportFragmentManager)
    }
}