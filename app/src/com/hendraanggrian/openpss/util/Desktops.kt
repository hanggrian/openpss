@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import ktfx.scene.control.styledErrorAlert
import java.awt.Desktop.getDesktop
import java.awt.Desktop.isDesktopSupported
import java.io.File
import java.net.URI

/** Open link in system's browser if it is supported. */
fun browseUrl(uri: String) {
    if (ensureDesktopAvailable()) getDesktop().browse(URI(uri))
}

/** Open file/folder in system's file explorer if it is supported. */
fun openFile(file: File) {
    if (ensureDesktopAvailable()) getDesktop().open(file)
}

private inline fun ensureDesktopAvailable(): Boolean = isDesktopSupported().also {
    if (!it) styledErrorAlert(getStyle(R.style.openpss), "Desktop is not supported.").show()
}