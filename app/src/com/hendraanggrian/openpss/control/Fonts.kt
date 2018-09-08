@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.getFont
import javafx.scene.text.Font
import javafx.scene.text.Font.font
import javafx.scene.text.FontWeight.BOLD
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX

fun bold(size: Number = -1): Font = when {
    IS_OS_MAC_OSX -> getFont(R.font.sf_pro_text_bold, size)
    else -> font(null, BOLD, size.toDouble())
}