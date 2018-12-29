package com.hendraanggrian.openpss.popup

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.bundler.bindExtras

class TextDialogFragment : DialogFragment() {

    @Extra lateinit var text: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindExtras()
        return AlertDialog.Builder(context!!)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
    }
}