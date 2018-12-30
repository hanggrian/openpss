package com.hendraanggrian.openpss.ui.main

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hendraanggrian.bundler.bindExtras
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.OpenPSSActivity
import com.hendraanggrian.openpss.ui.customer.CustomerFragment
import com.hendraanggrian.openpss.ui.invoice.InvoiceFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : OpenPSSActivity() {

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.tab_customer -> {
                replaceFragment(R.id.fragmentLayout, CustomerFragment())
                true
            }
            R.id.tab_invoice -> {
                replaceFragment(R.id.fragmentLayout, InvoiceFragment())
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindExtras()
        Toast.makeText(this, login.name, Toast.LENGTH_SHORT).show()
        navigationView.setOnNavigationItemSelectedListener(navigationListener)
    }
}