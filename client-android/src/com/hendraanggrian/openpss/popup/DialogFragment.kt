package com.hendraanggrian.openpss.popup

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.content.Activity
import com.hendraanggrian.openpss.content.AndroidComponent
import com.hendraanggrian.openpss.data.Employee

open class DialogFragment : AppCompatDialogFragment(), AndroidComponent {

    override val api: OpenPSSApi get() = (activity as Activity).api

    override val rootLayout: View get() = (activity as Activity).rootLayout

    override val login: Employee get() = (activity as Activity).login

    fun show(manager: FragmentManager) = show(manager, null)

    fun args(bundle: Bundle): DialogFragment = apply { arguments = bundle }
}