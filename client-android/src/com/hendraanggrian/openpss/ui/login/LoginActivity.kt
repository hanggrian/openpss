package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.Setting
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.ui.Activity
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : Activity() {

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Setting.KEY_LANGUAGE) {
                GlobalScope.launch {
                    delay(500)
                    ProcessPhoenix.triggerRebirth(this@LoginActivity)
                }
            }
            loginButton.isEnabled = !defaults[Setting.KEY_SERVER_HOST]!!.isBlank() &&
                !defaults[Setting.KEY_SERVER_PORT]!!.isBlank() &&
                !defaults[Setting.KEY_EMPLOYEE]!!.isBlank()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R2.string.openpss_login)
        loginButton.text = getString(R2.string.login)
        preferenceListener.onSharedPreferenceChanged(
            defaults.toSharedPreferences(),
            null
        ) // trigger once
        replaceFragment(R.id.preferenceLayout, LoginPreferenceFragment())
    }

    override fun onResume() {
        super.onResume()
        defaults.toSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        defaults.toSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun login(@Suppress("UNUSED_PARAMETER") view: View) {
        openPssApplication.api = OpenPSSApi(
            defaults[Setting.KEY_SERVER_HOST]!!,
            defaults.getInt(Setting.KEY_SERVER_PORT)
        )
        PasswordDialogFragment()
            .args(extrasOf<PasswordDialogFragment>(defaults[Setting.KEY_EMPLOYEE]!!))
            .show(supportFragmentManager)
    }
}
