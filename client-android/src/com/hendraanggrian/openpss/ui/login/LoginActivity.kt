package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.OpenPSSActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : OpenPSSActivity() {

    private lateinit var preferences: SharedPreferences

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, _ ->
            loginButton.isEnabled = !preferences.getString("employee", null).isNullOrBlank()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        replaceFragment(R.id.preferenceLayout, LoginFragment())
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
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

    fun login(@Suppress("UNUSED_PARAMETER") view: View) = PasswordDialogFragment()
        .args(extrasOf<PasswordDialogFragment>(preferences.getString("employee", null)!!))
        .show(supportFragmentManager)
}