package com.hendraanggrian.openpss.ui.login

import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.InputType
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.hendraanggrian.openpss.R

class PasswordDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            onFocusChangeListener = OnFocusChangeListener { view, _ ->
                view.post {
                    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
        val dialog = AlertDialog.Builder(context!!)
            .setTitle(R.string.password)
            .setView(FrameLayout(context!!).apply {
                addView(editText)
                val medium = context.resources.getDimensionPixelSize(R.dimen.padding_medium)
                val large = context.resources.getDimensionPixelSize(R.dimen.padding_large)
                (editText.layoutParams as ViewGroup.MarginLayoutParams).setMargins(large, medium, large, medium)
            })
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.login) { _, _ ->

            }
            .create()
        dialog.setOnShowListener { editText.requestFocus() }
        return dialog
    }
}