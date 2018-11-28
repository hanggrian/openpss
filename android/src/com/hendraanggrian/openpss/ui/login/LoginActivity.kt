package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.hendraanggrian.openpss.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, _ ->
        loginButton.isEnabled = !preferences.getString("employee", null).isNullOrBlank() &&
            !preferences.getString("server_host", null).isNullOrBlank() &&
            preferences.getString("server_port", null)?.toIntOrNull() != null &&
            !preferences.getString("server_user", null).isNullOrBlank() &&
            !preferences.getString("server_password", null).isNullOrBlank()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.preferenceLayout, LoginFragment())
            .commitNow()
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

    fun login(view: View) = PasswordDialogFragment().show(supportFragmentManager, null)
}