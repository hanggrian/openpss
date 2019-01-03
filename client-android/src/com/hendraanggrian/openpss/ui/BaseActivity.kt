package com.hendraanggrian.openpss.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.openpss.AndroidComponent
import com.hendraanggrian.openpss.AndroidSetting
import com.hendraanggrian.openpss.OpenPssApp
import com.hendraanggrian.openpss.api.OpenPssApi
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.nosql.StringId
import java.util.ResourceBundle

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), AndroidComponent {

    @Extra lateinit var loginName: String
    @Extra @JvmField var loginIsAdmin: Boolean = false
    @Extra lateinit var loginId: String

    private lateinit var _login: Employee

    override fun getContext(): Context? = this

    override val api: OpenPssApi get() = openPssApp.api

    override val rootLayout: View get() = findViewById(android.R.id.content)

    override val login: Employee
        get() {
            if (!::_login.isInitialized) {
                _login = Employee(loginName, "", loginIsAdmin).apply { id = StringId(loginId) }
            }
            return _login
        }

    override val resourceBundle: ResourceBundle get() = openPssApp.resourceBundle

    override val setting: AndroidSetting get() = openPssApp.setting

    inline val openPssApp: OpenPssApp get() = application as OpenPssApp

    fun AppCompatActivity.replaceFragment(@IdRes containerViewId: Int, fragment: Fragment) =
        supportFragmentManager.beginTransaction()
            .replace(containerViewId, fragment)
            .commitNow()
}