package com.hendraanggrian.openpss.ui.login

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.hendraanggrian.openpss.R
import com.takisoft.preferencex.PreferenceFragmentCompat

class LoginFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_login)
        find<EditTextPreference>("employee").bindSummary({ text })
        find<EditTextPreference>("server_host").bindSummary({ text })
        find<EditTextPreference>("server_port").bindSummary({ text })
        find<EditTextPreference>("server_user").bindSummary({ text })
        find<com.takisoft.preferencex.EditTextPreference>("server_password") {
            bindSummary({ text }) { it?.toList()?.joinToString("") { "•" } }
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Preference> find(key: CharSequence): T =
        findPreference(key) as T

    private inline fun <T : Preference> find(key: CharSequence, block: T.() -> Unit): T =
        find<T>(key).apply(block)

    /**
     * @param initial starting value can be obtained from its value, text, etc.
     * @param convert its preference value to representable summary text.
     */
    private fun <P : Preference, T> P.bindSummary(
        initial: P.() -> T?,
        convert: (T?) -> CharSequence? = { it?.toString() }
    ) {
        initial()?.let { summary = convert(it) }
        onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            @Suppress("UNCHECKED_CAST")
            preference.summary = convert(newValue as? T)
            true
        }
    }
}