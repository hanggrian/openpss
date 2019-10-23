package com.hendraanggrian.openpss.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.DialogPreference
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.bundler.bindExtras
import com.hendraanggrian.defaults.SharedPreferencesDefaults
import com.hendraanggrian.openpss.AndroidComponent
import com.hendraanggrian.openpss.schema.Employee
import com.takisoft.preferencex.PreferenceFragmentCompat
import java.util.ResourceBundle

open class BaseFragment : Fragment(), AndroidComponent {

    override val defaults: SharedPreferencesDefaults get() = openpssActivity.defaults

    override val rootLayout: View get() = openpssActivity.rootLayout

    override val login: Employee get() = openpssActivity.login

    override val resourceBundle: ResourceBundle get() = openpssActivity.resourceBundle

    inline val openpssActivity: Activity get() = activity as Activity
}

abstract class BasePreferenceFragment : PreferenceFragmentCompat(), AndroidComponent {

    override val defaults: SharedPreferencesDefaults get() = openpssActivity.defaults

    override val rootLayout: View get() = openpssActivity.rootLayout

    override val login: Employee get() = openpssActivity.login

    override val resourceBundle: ResourceBundle get() = openpssActivity.resourceBundle

    inline val openpssActivity: Activity get() = activity as Activity

    inline var DialogPreference.titleAll: CharSequence?
        get() = title
        set(value) {
            title = value
            dialogTitle = value
        }
}

open class BaseDialogFragment : AppCompatDialogFragment(), AndroidComponent {

    override val defaults: SharedPreferencesDefaults get() = openpssActivity.defaults

    override val rootLayout: View get() = openpssActivity.rootLayout

    override val login: Employee get() = openpssActivity.login

    override val resourceBundle: ResourceBundle get() = openpssActivity.resourceBundle

    inline val openpssActivity: Activity get() = activity as Activity

    fun show(manager: FragmentManager) = show(manager, null)

    fun args(bundle: Bundle): BaseDialogFragment = apply { arguments = bundle }
}

class TextDialogFragment : BaseDialogFragment() {

    @Extra lateinit var text: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindExtras()
        return AlertDialog.Builder(context!!)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
    }
}
