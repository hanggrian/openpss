package com.hendraanggrian.openpss.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.defaults.SharedPreferencesDefaults
import com.hendraanggrian.openpss.AndroidComponent
import com.hendraanggrian.openpss.OpenPSSApplication
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.Employee
import java.util.ResourceBundle

@SuppressLint("Registered")
open class Activity : AppCompatActivity(), AndroidComponent {

    @Extra lateinit var loginName: String
    @Extra @JvmField var loginIsAdmin: Boolean = false
    @Extra lateinit var loginId: String

    private lateinit var _login: Employee

    override fun getContext(): Context? = this

    override val api: OpenPSSApi get() = openPssApplication.api

    override val rootLayout: View get() = findViewById(android.R.id.content)

    override val login: Employee
        get() {
            if (!::_login.isInitialized) {
                _login = Employee(loginName, "", loginIsAdmin)
                    .apply { id = StringId(loginId) }
            }
            return _login
        }

    override val resourceBundle: ResourceBundle get() = openPssApplication.resourceBundle

    override val defaults: SharedPreferencesDefaults get() = openPssApplication.defaults

    inline val openPssApplication: OpenPSSApplication get() = application as OpenPSSApplication

    fun replaceFragment(@IdRes containerViewId: Int, fragment: Fragment) =
        supportFragmentManager.beginTransaction()
            .replace(containerViewId, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commitNow()
}
