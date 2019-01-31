package com.hendraanggrian.openpss.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.Setting
import com.hendraanggrian.openpss.api.OpenPssApi
import com.hendraanggrian.openpss.ui.BaseActivity
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

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
            defaults.sharedPreferences,
            null
        ) // trigger once
        replaceFragment(R.id.preferenceLayout, LoginPreferenceFragment())
    }

    override fun onResume() {
        super.onResume()
        defaults.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        defaults.sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun login(@Suppress("UNUSED_PARAMETER") view: View) {
        openPssApplication.api = OpenPssApi(
            defaults[Setting.KEY_SERVER_HOST]!!,
            defaults.getInt(Setting.KEY_SERVER_PORT)
        )
        PasswordDialogFragment()
            .args(extrasOf<PasswordDialogFragment>(defaults[Setting.KEY_EMPLOYEE]!!))
            .show(supportFragmentManager)
    }
}