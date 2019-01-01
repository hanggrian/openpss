package com.hendraanggrian.openpss.ui.login

import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.bundler.bindExtras
import com.hendraanggrian.bundler.extrasOf
import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.OpenPssDialogFragment
import com.hendraanggrian.openpss.ui.TextDialogFragment
import com.hendraanggrian.openpss.ui.main.MainActivity
import kotlinx.coroutines.runBlocking

class PasswordDialogFragment : OpenPssDialogFragment() {

    @Extra lateinit var employee: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindExtras()
        val editText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            onFocusChangeListener = OnFocusChangeListener { view, _ ->
                view.post {
                    val inputMethodManager =
                        context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
        val manager = activity!!.supportFragmentManager
        val dialog = AlertDialog.Builder(context!!)
            .setTitle(R.string.password)
            .setView(FrameLayout(context!!).apply {
                addView(editText)
                val medium = context.resources.getDimensionPixelSize(R.dimen.padding_medium)
                val large = context.resources.getDimensionPixelSize(R.dimen.padding_large)
                (editText.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                    large,
                    medium,
                    large,
                    medium
                )
            })
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.login) { _, _ ->
                val login = runCatching {
                    runBlocking { api.login(employee, editText.text) }
                }.onFailure {
                    if (BuildConfig2.DEBUG) it.printStackTrace()
                    TextDialogFragment()
                        .args(extrasOf<TextDialogFragment>(it.message.toString()))
                        .show(manager)
                }.getOrThrow()
                startActivity(
                    Intent(context, MainActivity::class.java)
                        .putExtras(extrasOf<MainActivity>(login))
                )
                activity!!.finish()
            }
            .create()
        dialog.setOnShowListener { editText.requestFocus() }
        return dialog
    }
}