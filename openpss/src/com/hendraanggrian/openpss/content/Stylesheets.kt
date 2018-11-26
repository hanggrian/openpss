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

val STYLESHEET_PRINT_TREETABLEVIEW = getStyle(R.style.css_print_treetableview)

val STYLESHEET_INVOICE = getStyle(R.style.css_invoice)