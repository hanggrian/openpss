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
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.ui.BaseDialogFragment
import com.hendraanggrian.openpss.ui.TextDialogFragment
import com.hendraanggrian.openpss.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PasswordDialogFragment : BaseDialogFragment() {

    @Extra lateinit var loginName: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindExtras()
        val editText = EditText(context).apply {
            if (BuildConfig2.DEBUG) setText(Employee.DEFAULT_PASSWORD)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            onFocusChangeListener = OnFocusChangeListener { view, _ ->
                view.post {
                    val inputMethodManager =
                        context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
        val manager = openpssActivity.supportFragmentManager
        val dialog = AlertDialog.Builder(context!!)
            .setTitle(getString(R2.string.password))
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
            .setPositiveButton(getString(R2.string.login)) { _, _ ->
                runBlocking {
                    runCatching {
                        val login = withContext(Dispatchers.IO) {
                            OpenPSSApi.login(loginName, editText.text)
                        }
                        if (login == Employee.NOT_FOUND) error(getString(R2.string.login_failed))
                        startActivity(
                            Intent(context, MainActivity::class.java).putExtras(
                                extrasOf<MainActivity>(
                                    login.name,
                                    login.isAdmin,
                                    login.id.value
                                )
                            )
                        )
                        openpssActivity.finish()
                    }.onFailure {
                        if (BuildConfig2.DEBUG) it.printStackTrace()
                        TextDialogFragment()
                            .args(extrasOf<TextDialogFragment>(it.message.toString()))
                            .show(manager)
                    }
                }
            }
            .create()
        dialog.setOnShowListener { editText.requestFocus() }
        return dialog
    }
}
