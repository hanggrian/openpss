package com.hendraanggrian.openpss.io

import java.io.File

abstract class Directory : File {

    constructor(parent: String, child: String) : super(parent, child)

    constructor(parent: Directory, child: String) : super(parent, child)

    init {
        check(isDirectory)
        mkdirs()
    }

    final override fun mkdirs(): Boolean = super.mkdirs()
}