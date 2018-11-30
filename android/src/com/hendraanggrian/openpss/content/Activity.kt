package com.hendraanggrian.openpss.content

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.openpss.db.schemas.Employee

@SuppressLint("Registered")
open class Activity : AppCompatActivity(), EmployeeContainer {

    @Extra override lateinit var login: Employee
}