package com.hendraanggrian.openpss.content

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.openpss.db.schemas.Employee

@SuppressLint("Registered")
open class Activity : AppCompatActivity(), AndroidComponent {

    override val context: Context get() = this

    override val rootLayout: View get() = findViewById(android.R.id.content)

    @Extra override lateinit var login: Employee
}