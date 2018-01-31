package com.wijayaprinting.io

import com.wijayaprinting.BuildConfig.ARTIFACT
import org.apache.commons.lang3.SystemUtils.USER_HOME
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now
import java.io.File

sealed class Folder : File {
    constructor(parent: String, child: String) : super(parent, child)
    constructor(parent: Folder, child: String) : super(parent, child)

    init {
        @Suppress("LeakingThis") mkdirs()
    }
}

object MainFolder : Folder(USER_HOME, ".$ARTIFACT")

object WageFolder : Folder(MainFolder, "wage")

open class WageContentFolder(date: LocalDate) : Folder(WageFolder, date.toString("yyyy-MM-dd")) {
    companion object : WageContentFolder(now())
}