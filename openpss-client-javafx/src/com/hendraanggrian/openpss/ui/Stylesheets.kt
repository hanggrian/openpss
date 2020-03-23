package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.getStyle

object Stylesheets {

    val OPENPSS = listOf(
        R.style._openpss,
        R.style._openpss_background,
        R.style._openpss_button,
        R.style._openpss_drawer,
        R.style._openpss_font,
        R.style._openpss_listview,
        R.style._openpss_tab
    ).map { getStyle(it) }

    val INVOICE = getStyle(R.style._invoice)

    val WAGE_RECORD = getStyle(R.style._wage_record)
}
