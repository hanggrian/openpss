package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.getStyle

val STYLESHEET_OPENPSS = listOf(
    R.style._openpss,
    R.style._openpss_background,
    R.style._openpss_button,
    R.style._openpss_drawer,
    R.style._openpss_font,
    R.style._openpss_listview,
    R.style._openpss_tab
).map { getStyle(it) }

val STYLESHEET_PRINT_TREETABLEVIEW = getStyle(R.style._print_treetableview)
