package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.getStyle

val STYLESHEET_OPENPSS = listOf(
    R.style.openpss,
    R.style.openpss_background,
    R.style.openpss_font
).map { getStyle(it) }

val STYLESHEET_PRINT_TREETABLEVIEW = getStyle(R.style.print_treetableview)