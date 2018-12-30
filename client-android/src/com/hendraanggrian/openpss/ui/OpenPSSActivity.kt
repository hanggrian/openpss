package com.hendraanggrian.openpss.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.openpss.OpenPSSApplication
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.data.Employee

@SuppressLint("Registered")
open class OpenPSSActivity : AppCompatActivity(), AndroidComponent {

    override fun getContext(): Context? = this

    override val api: OpenPSSApi get() = (application as OpenPSSApplication).getApi()

    override val rootLayout: View get() = findViewById(android.R.id.content)

    @Extra override lateinit var login: Employee

    fun AppCompatActivity.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment) =
        supportFragmentManager.beginTransaction()
            .replace(containerViewId, fragment)
            .commitNow()
}