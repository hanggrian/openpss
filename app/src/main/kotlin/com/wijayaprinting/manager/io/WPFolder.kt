package com.wijayaprinting.manager.io

import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import java.io.File
import java.lang.Boolean.TRUE
import java.nio.file.Files.getAttribute
import java.nio.file.Files.setAttribute
import java.nio.file.LinkOption.NOFOLLOW_LINKS

class WPFolder : File("${System.getProperty("user.home")}$separator.wp") {

    init {
        super.mkdirs()
        if (IS_OS_WINDOWS) {
            val path = toPath()
            val hidden = getAttribute(path, "dos:hidden", NOFOLLOW_LINKS) as Boolean
            if (!hidden) {
                setAttribute(path, "dos:hidden", TRUE, NOFOLLOW_LINKS)
            }
        }
    }
}