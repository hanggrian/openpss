package com.hendraanggrian.openpss.ui.main

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hendraanggrian.bundler.bindExtras
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.BaseActivity
import com.hendraanggrian.openpss.ui.customer.CustomerFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        replaceFragment(
            R.id.fragmentLayout, when (item.itemId) {
                R.id.tab_customer -> CustomerFragment()
                R.id.tab_invoice -> CustomerFragment()
                R.id.tab_schedule -> CustomerFragment()
                else -> CustomerFragment()
            }
        )
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindExtras()
        Toast.makeText(this, login.name, Toast.LENGTH_SHORT).show()
        navigationView.setOnNavigationItemSelectedListener(navigationListener)
    }
}