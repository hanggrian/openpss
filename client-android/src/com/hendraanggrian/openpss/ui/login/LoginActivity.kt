package com.hendraanggrian.openpss.ui.login

import android.os.Bundle
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.OpenPssActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : OpenPssActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
    }
}