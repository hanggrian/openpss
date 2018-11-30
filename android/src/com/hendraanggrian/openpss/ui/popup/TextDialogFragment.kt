package com.hendraanggrian.openpss.ui.popup

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.bundler.Extra

class TextDialogFragment : AppCompatDialogFragment() {

    @Extra lateinit var text: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Bundler.bindExtras(this)
        return AlertDialog.Builder(context!!)
            .setMessage(text)
            .create()
    }
}