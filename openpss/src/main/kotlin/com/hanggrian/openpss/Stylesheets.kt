package com.hanggrian.openpss

import com.hanggrian.openpss.util.getStyle

val STYLESHEET_OPENPSS =
    listOf(
        R.style_openpss,
        R.style_openpss_background,
        R.style_openpss_button,
        R.style_openpss_drawer,
        R.style_openpss_fonts,
        R.style_openpss_listview,
        R.style_openpss_tab,
    ).map { getStyle(it) }

val STYLESHEET_PRINT_TREETABLEVIEW = getStyle(R.style_print_treetableview)
