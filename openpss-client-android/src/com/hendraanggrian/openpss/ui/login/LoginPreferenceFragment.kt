package com.hendraanggrian.openpss.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.Setting
import com.hendraanggrian.openpss.ui.BasePreferenceFragment
import com.takisoft.preferencex.EditTextPreference

class LoginPreferenceFragment : BasePreferenceFragment() {

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_login)
        find<ListPreference>(Setting.KEY_LANGUAGE) {
            titleAll = getString(R2.string.language)
            bindSummary({ Language.ofFullCode(value) })
            val languages = Language.values()
            entries = languages.map { it.toString() }.toTypedArray()
            entryValues = languages.map { it.fullCode }.toTypedArray()
        }
        find<EditTextPreference>(Setting.KEY_SERVER_HOST) {
            titleAll = getString(R2.string.server_host)
            editText.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            bindSummary({ text })
        }
        find<EditTextPreference>(Setting.KEY_SERVER_PORT) {
            titleAll = getString(R2.string.server_port)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            bindSummary({ text })
        }
        find<EditTextPreference>(Setting.KEY_EMPLOYEE) {
            titleAll = getString(R2.string.employee)
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            bindSummary({ text })
        }
        find<Preference>("about") {
            parent!!.title = getString(R2.string.about)
            title = "OpenPSS ${BuildConfig2.VERSION}"
            summary = "Tap to visit"
            setOnPreferenceClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(BuildConfig2.WEBSITE)))
                true
            }
        }
    }

    private inline fun <T : Preference> find(key: CharSequence, block: T.() -> Unit): T =
        findPreference<T>(key)!!.apply(block)

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
