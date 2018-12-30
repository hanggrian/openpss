package com.hendraanggrian.openpss.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.bundler.bindExtras
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.data.Employee

open class OpenPssFragment : Fragment(), AndroidComponent {

    override val api: OpenPSSApi get() = (activity as OpenPssActivity).api

    override val rootLayout: View get() = (activity as OpenPssActivity).rootLayout

    override val login: Employee get() = (activity as OpenPssActivity).login
}

open class OpenPssDialogFragment : AppCompatDialogFragment(), AndroidComponent {

    override val api: OpenPSSApi get() = (activity as OpenPssActivity).api

    override val rootLayout: View get() = (activity as OpenPssActivity).rootLayout

    override val login: Employee get() = (activity as OpenPssActivity).login

    fun show(manager: FragmentManager) = show(manager, null)

    fun args(bundle: Bundle): OpenPssDialogFragment = apply { arguments = bundle }
}

class TextDialogFragment : OpenPssDialogFragment() {

    @Extra lateinit var text: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindExtras()
        return AlertDialog.Builder(context!!)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
    }
}