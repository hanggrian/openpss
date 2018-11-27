package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.getStyle

val STYLESHEET_OPENPSS = listOf(
    R.style.css_openpss,
    R.style.css_openpss_background,
    R.style.css_openpss_button,
    R.style.css_openpss_drawer,
    R.style.css_openpss_font,
    R.style.css_openpss_listview,
    R.style.css_openpss_tab
).map { getStyle(it) }

val STYLESHEET_INVOICE = getStyle(R.style.css_invoice)

val STYLESHEET_WAGE_RECORD = getStyle(R.style.css_wage_record)