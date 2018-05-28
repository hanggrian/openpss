@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import javafx.scene.text.Font
import javafx.scene.text.Font.font
import javafx.scene.text.FontWeight.BOLD
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX

inline fun bold(size: Number = -1): Font = when {
    IS_OS_MAC_OSX -> getFont(R.font.sf_pro_text_bold, size)
    else -> font(null, BOLD, size.toDouble())
}