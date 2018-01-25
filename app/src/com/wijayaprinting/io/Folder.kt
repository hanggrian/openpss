package com.wijayaprinting.io

import com.wijayaprinting.BuildConfig.ARTIFACT
import org.apache.commons.lang3.SystemUtils.USER_HOME
import java.io.File

@Suppress("LeakingThis")
sealed class Folder(name: String) : File(name) {
    init {
        mkdirs()
    }
}

object HiddenFolder : Folder("$USER_HOME$separator.$ARTIFACT")
object DesktopFolder : Folder("$USER_HOME${separator}Desktop")