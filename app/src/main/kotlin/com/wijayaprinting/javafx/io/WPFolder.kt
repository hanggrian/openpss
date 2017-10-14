package com.wijayaprinting.javafx.io

import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption

class WPFolder : File("${System.getProperty("user.home")}$separator.wp") {

    init {
        super.mkdirs()
        if (IS_OS_WINDOWS) {
            val path = toPath()
            val hidden = Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS) as Boolean
            if (!hidden) {
                Files.setAttribute(path, "dos:hidden", java.lang.Boolean.TRUE, LinkOption.NOFOLLOW_LINKS)
            }
        }
    }
}