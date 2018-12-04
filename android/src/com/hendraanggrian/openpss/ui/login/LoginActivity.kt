package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.popup.args
import com.hendraanggrian.openpss.popup.show
import com.hendraanggrian.openpss.util.replaceFragment
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
        .args(
            extrasOf<PasswordDialogFragment>(
                preferences.getString("server_host", null)!!,
                preferences.getString("server_port", null)!!,
                preferences.getString("server_user", null)!!,
                preferences.getString("server_password", null)!!,
                preferences.getString("employee", null)!!
            )
        )
        .show(supportFragmentManager)
}