package com.wijayaprinting.javafx.io

import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class HomeFolder : File("${System.getProperty("user.home")}$separator.wp") {

    init {
        super.mkdirs()
        if (SystemUtils.IS_OS_WINDOWS) {
            val path = toPath()
            val hidden = Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS) as Boolean
            if (!hidden) {
                Files.setAttribute(path, "dos:hidden", java.lang.Boolean.TRUE, LinkOption.NOFOLLOW_LINKS)
            }
        }
    }
}