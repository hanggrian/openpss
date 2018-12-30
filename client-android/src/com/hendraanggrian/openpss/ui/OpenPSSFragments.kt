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

open class OpenPSSFragment : Fragment(), AndroidComponent {

    override val api: OpenPSSApi get() = (activity as OpenPSSActivity).api

    override val rootLayout: View get() = (activity as OpenPSSActivity).rootLayout

    override val login: Employee get() = (activity as OpenPSSActivity).login
}

open class OpenPSSDialogFragment : AppCompatDialogFragment(), AndroidComponent {

    override val api: OpenPSSApi get() = (activity as OpenPSSActivity).api

    override val rootLayout: View get() = (activity as OpenPSSActivity).rootLayout

    override val login: Employee get() = (activity as OpenPSSActivity).login

    fun show(manager: FragmentManager) = show(manager, null)

    fun args(bundle: Bundle): OpenPSSDialogFragment = apply { arguments = bundle }
}

class TextDialogFragment : OpenPSSDialogFragment() {

    @Extra lateinit var text: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindExtras()
        return AlertDialog.Builder(context!!)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
    }
}