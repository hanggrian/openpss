package com.hendraanggrian.openpss.content

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.data.Employee

@SuppressLint("Registered")
open class Activity : AppCompatActivity(), AndroidComponent {

    override fun getContext(): Context? = this

    override val api: OpenPSSApi get() = (application as App).getApi()

    override val rootLayout: View get() = findViewById(android.R.id.content)

    @Extra override lateinit var login: Employee
}