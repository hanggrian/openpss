package com.wijayaprinting.utils

import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import java.io.File
import java.nio.file.Files.getAttribute
import java.nio.file.Files.setAttribute
import java.nio.file.LinkOption.NOFOLLOW_LINKS

fun File.createIfNotExists() {
    if (!exists()) createNewFile()
}

fun File.hideOnWindows() {
    if (IS_OS_WINDOWS) toPath().let { path ->
        val isHidden = getAttribute(path, "dos:hidden", NOFOLLOW_LINKS) as? Boolean
        if (isHidden != null && !isHidden) setAttribute(path, "dos:hidden", true, NOFOLLOW_LINKS)
    }
}